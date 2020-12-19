package pl.polsl.expensis_mobile.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import org.json.JSONObject
import pl.polsl.expensis_mobile.dto.RefreshTokenDTO
import pl.polsl.expensis_mobile.rest.BASE_URL
import pl.polsl.expensis_mobile.rest.Endpoint
import pl.polsl.expensis_mobile.rest.VolleySingleton
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.accessTokenConst
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.clearAllSharedPreferences
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.getRefreshToken
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.storeTokens

class TokenUtils {
    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context = con
        }

        fun refreshToken() {
            val refreshToken = getRefreshToken() ?: return
            val url = BASE_URL + Endpoint.REFRESH
            val refreshTokenJsonObject = JSONObject(Gson().toJson(RefreshTokenDTO(refreshToken)))

            val objectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                refreshTokenJsonObject,
                { response ->
                    storeTokens(response.get(accessTokenConst) as String, refreshToken)
                },
                { error ->
                    println("Cannot refresh token, error:  $error")
                    clearAllSharedPreferences()
                }
            )
            VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
        }
    }
}