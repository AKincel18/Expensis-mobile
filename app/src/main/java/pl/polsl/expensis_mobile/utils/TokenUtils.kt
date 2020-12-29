package pl.polsl.expensis_mobile.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import org.json.JSONObject
import pl.polsl.expensis_mobile.activities.MenuActivity
import pl.polsl.expensis_mobile.dto.RefreshTokenDTO
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.Messages.Companion.SESSION_EXPIRED
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.accessTokenConst
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.clearAllSharedPreferences
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.getRefreshToken
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.storeTokens

class TokenUtils {
    companion object {

        private lateinit var context: Context
        lateinit var refreshToken: String
        fun setContext(con: Context) {
            context = con
        }

        fun refreshToken(callback: ServerCallback<JSONObject>) {
            refreshToken = getRefreshToken() ?: return
            val url = BASE_URL + Endpoint.REFRESH
            val refreshTokenJsonObject = JSONObject(Gson().toJson(RefreshTokenDTO(refreshToken)))
            val volleyService = VolleyService(context, callback)
            volleyService.requestObject(Request.Method.POST, url, refreshTokenJsonObject)
        }

        fun refreshTokenOnFailure(error: VolleyError) {
            println("Cannot refresh token, error:  $error")
            clearAllSharedPreferences()
            Toast.makeText(context, SESSION_EXPIRED ,Toast.LENGTH_SHORT).show()
        }
    }
}