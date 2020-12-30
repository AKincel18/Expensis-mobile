package pl.polsl.expensis_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.expenses_activity.*
import kotlinx.android.synthetic.main.register_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.ExpensesAdapter
import pl.polsl.expensis_mobile.models.Expense
import pl.polsl.expensis_mobile.others.LoadingAction
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.clearAllSharedPreferences
import pl.polsl.expensis_mobile.utils.TokenUtils
import pl.polsl.expensis_mobile.utils.Utils.Companion.getGsonWithLocalDateTime

class ExpensesActivity : AppCompatActivity(), LoadingAction {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expenses_activity)
        fetchExpensesCallback()
    }

    private fun fetchExpensesCallback() {
        fetchExpenses(object : ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {
                val type = object : TypeToken<List<Expense>>() {}.type
                val expenses =
                    getGsonWithLocalDateTime().fromJson<List<Expense>>(response.toString(), type)
                val expensesAdapter = ExpensesAdapter()
                recycler_view.adapter = expensesAdapter
                expensesAdapter.submitList(expenses)
                expensesProgressBar.visibility = View.INVISIBLE
            }

            override fun onFailure(error: VolleyError) {
                if (error.networkResponse.statusCode == 403) {
                    refreshTokenCallback()
                } else {
                    val serverError = ServerErrorResponse(error)
                    val messageError = serverError.getErrorResponse()
                    expensesProgressBar.visibility = View.INVISIBLE
                    handleError(messageError)
                }
            }
        })
    }

    private fun refreshTokenCallback(){
        TokenUtils.refreshToken(object : ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                SharedPreferencesUtils.storeTokens(
                    response.get(SharedPreferencesUtils.accessTokenConst) as String,
                    TokenUtils.refreshToken,
                    null
                )
                startActivity(Intent(applicationContext, MenuActivity::class.java))
            }

            override fun onFailure(error: VolleyError) {
                TokenUtils.refreshTokenOnFailure(error)
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        })
    }

    private fun fetchExpenses(callback: ServerCallback<JSONArray>) {
        val url = BASE_URL + Endpoint.EXPENSES
        val volleyService = VolleyService(callback, this)
        volleyService.requestArray(Request.Method.GET, url, null)
    }

    private fun handleError(messageError: String?) {
        val intent = Intent(applicationContext, MenuActivity::class.java)
        intent.putExtra(IntentKeys.RESPONSE_ERROR, messageError)
        startActivity(intent)
    }

    override fun showProgressBar() {
        Thread(Runnable {
            this.runOnUiThread {
                registerProgressBar.visibility = View.VISIBLE
            }
        }).start()
    }

    override fun changeEditableFields(isEnabled: Boolean) {}

    fun onGoBackClicked(view: View) {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}