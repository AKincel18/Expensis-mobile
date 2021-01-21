package pl.polsl.expensis_mobile.activities.expenses

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.expenses_activity.*
import kotlinx.android.synthetic.main.register_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.activities.LoginActivity
import pl.polsl.expensis_mobile.activities.MenuActivity
import pl.polsl.expensis_mobile.adapters.ExpensesAdapter
import pl.polsl.expensis_mobile.models.Expense
import pl.polsl.expensis_mobile.others.LoadingAction
import pl.polsl.expensis_mobile.others.LoggedUser
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.TokenUtils
import pl.polsl.expensis_mobile.utils.Utils.Companion.getGsonWithLocalDate
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime


class ExpensesActivity : AppCompatActivity(), LoadingAction, NumberPicker.OnValueChangeListener {

    private lateinit var loggedUserDateJoined: LocalDateTime
    private val currentDate = LocalDate.now()
    private var isFilterOpen: Boolean = false
    private val monthNames = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expenses_activity)
        initFilterTab()
        fetchExpensesSumCallback()
        fetchExpensesCallback()
    }

    private fun initFilterTab() {
        applyFilterButton.visibility = View.GONE
        initMonthPickers()
    }

    private fun initMonthPickers() {
        loggedUserDateJoined = LoggedUser().serialize()!!.dateJoined

        yearPicker.minValue = loggedUserDateJoined.year
        yearPicker.maxValue = currentDate.year
        yearPicker.value = currentDate.year
        yearPicker.wrapSelectorWheel = false
        yearPicker.visibility = View.GONE
        yearPicker.setOnValueChangedListener(this)

        determineMonthBoundries(currentDate.year)
        monthPicker.displayedValues = monthNames
        monthPicker.wrapSelectorWheel = false
        monthPicker.visibility = View.GONE
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
       determineMonthBoundries(newVal)
    }

    private fun determineMonthBoundries(year: Int) {
        when (year) {
            currentDate.year -> {
                monthPicker.displayedValues = monthNames
                monthPicker.minValue = 1
                monthPicker.maxValue = currentDate.monthValue
                monthPicker.displayedValues = monthNames.sliceArray(0 until currentDate.monthValue)
                monthPicker.value = 1
            }
            loggedUserDateJoined.year -> {
                monthPicker.displayedValues = monthNames
                monthPicker.minValue = loggedUserDateJoined.monthValue
                monthPicker.maxValue = 12
                monthPicker.displayedValues = monthNames.sliceArray(loggedUserDateJoined.monthValue-1 until 12)
                monthPicker.value = 12
            }
            else -> {
                monthPicker.displayedValues = monthNames
                monthPicker.minValue = 1
                monthPicker.maxValue = 12
            }
        }
    }

    private fun fetchExpensesCallback() {
        fetchExpenses(object : ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {
                val type = object : TypeToken<List<Expense>>() {}.type
                val expenses =
                    getGsonWithLocalDate().fromJson<List<Expense>>(response.toString(), type)
                val expensesAdapter = ExpensesAdapter { expense -> adapterOnClick(expense) }
                recycler_view.adapter = expensesAdapter
                expensesAdapter.submitList(expenses)
                expensesProgressBar.visibility = View.INVISIBLE
            }

            override fun onFailure(error: VolleyError) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
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

    private fun fetchExpensesSumCallback() {
        fetchExpensesSum(object: ServerCallback<String> {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: String) {
                expensesSumText.text = "-$response"
            }

            override fun onFailure(error: VolleyError) {
                expensesSumText.text = "0"
            }
        })
    }

    private fun refreshTokenCallback() {
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
        val url = BASE_URL + Endpoint.EXPENSES + "?month=${monthPicker.value}&year=${yearPicker.value}"
        VolleyService().requestArray(Request.Method.GET, url, null, callback, this)
    }

    private fun fetchExpensesSum(callback: ServerCallback<String>) {
        val url = BASE_URL + Endpoint.EXPENSES_SUM + "?month=${monthPicker.value}&year=${yearPicker.value}"
        VolleyService().requestString(Request.Method.GET, url, callback, this)
    }

    private fun handleError(messageError: String?) {
        val intent = Intent(applicationContext, MenuActivity::class.java)
        intent.putExtra(IntentKeys.RESPONSE_ERROR, messageError)
        startActivity(intent)
    }

    override fun showProgressBar() {
        Thread {
            this.runOnUiThread {
                registerProgressBar.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun changeEditableFields(isEnabled: Boolean) {}

    fun onGoBackClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(this, MenuActivity::class.java))
    }

    /* Opens ExpenseDetailsActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(expense: Expense) {
        val intent = Intent(this, ExpenseDetailsActivity::class.java)
        intent.putExtra(
            IntentKeys.EXPENSE_DETAIL,
            expense as Serializable
        )
        startActivity(intent)
    }

    fun onAddClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(this, AddExpenseActivity::class.java))
    }

    fun onFilterClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        if (isFilterOpen) {
            monthPicker.visibility = View.GONE
            yearPicker.visibility = View.GONE
            applyFilterButton.visibility = View.GONE
        } else {
            monthPicker.visibility = View.VISIBLE
            yearPicker.visibility = View.VISIBLE
            applyFilterButton.visibility = View.VISIBLE
        }
        isFilterOpen = !isFilterOpen
    }

    @SuppressLint("SetTextI18n")
    fun applyFilters(@Suppress("UNUSED_PARAMETER") view: View) {
        expensesProgressBar.visibility = View.VISIBLE
        if (yearPicker.value == currentDate.year && monthPicker.value == currentDate.monthValue) {
            dateText.text = this.resources.getString(R.string.current_month_expenses)
        } else {
            dateText.text = monthNames[monthPicker.value-1] + " " + yearPicker.value
        }
        fetchExpensesSumCallback()
        fetchExpensesCallback()
        onFilterClicked(filterButton)
    }

}