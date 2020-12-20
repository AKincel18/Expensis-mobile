package pl.polsl.expensis_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.dto.LoginDTO
import pl.polsl.expensis_mobile.dto.LoginFormDTO
import pl.polsl.expensis_mobile.others.LoggedUser
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.accessTokenConst
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.isTokenPresent
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.refreshTokenConst
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.storeTokens
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.userConst
import pl.polsl.expensis_mobile.utils.TokenUtils
import pl.polsl.expensis_mobile.utils.TokenUtils.Companion.refreshTokenCallback
import pl.polsl.expensis_mobile.utils.Utils.Companion.createUserJsonBuilder
import pl.polsl.expensis_mobile.validators.LoginValidator

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesUtils.setContext(this)
        TokenUtils.setContext(this)
        if (isTokenPresent()) {
            refreshTokenCallback()
            startActivity(Intent(this, MenuActivity::class.java))
        } else {
            setContentView(R.layout.login_activity)
            checkIntent()
            onLoginClickedCallback()
        }
    }

    private fun checkIntent() {
        val intent: Intent = intent
        intent.extras
        if (intent.hasExtra(IntentKeys.REGISTERED)) {
            showToast(intent.getStringExtra(IntentKeys.REGISTERED))
        }
        if (intent.hasExtra(IntentKeys.RESPONSE_ERROR)){
            showToast(intent.getStringExtra(IntentKeys.RESPONSE_ERROR))
        }
    }

    fun onRegisterClicked(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun onLoginClicked(callback: ServerCallback<JSONObject>) {
        loginButton.setOnClickListener {
            val loginFormDTO = LoginFormDTO(emailInput, passwordInput)
            val loginValidator = LoginValidator()
            val validationResult = loginValidator.validate(loginFormDTO)

            if (validationResult.isValid) {
                val loginDTO = LoginDTO(loginFormDTO)
                val userJsonObject = JSONObject(Gson().toJson(loginDTO))
                val url = BASE_URL + Endpoint.AUTH
                val volleyService = VolleyService(this, callback)
                volleyService.requestObject(Request.Method.POST, url, userJsonObject)
            } else {
                Toast.makeText(this, validationResult.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onLoginClickedCallback() {
        onLoginClicked(object: ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                processResponse(response)
                startMenuActivity()
            }

            override fun onFailure(error: VolleyError) {
                val serverErrorResponse = ServerErrorResponse(error)
                val errorMessage = serverErrorResponse.getErrorResponse()
                if (errorMessage != null) {
                    showToast(errorMessage)
                }
            }

        })
    }

    private fun startMenuActivity() {
        startActivity(Intent(this, MenuActivity::class.java))
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun processResponse(response: JSONObject) {
        val accessToken: String = response.get(accessTokenConst) as String
        val refreshToken: String = response.get(refreshTokenConst) as String
        val userObj = response.get(userConst) as JSONObject

        val loggedUser = createUserJsonBuilder().fromJson<LoggedUser>(
            userObj.toString(),
            LoggedUser::class.java
        )

        println(loggedUser.toString())

        storeTokens(accessToken, refreshToken)
        println("Rest response = $response")
    }


}