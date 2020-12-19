package pl.polsl.expensis_mobile.utils

import android.content.Context

class SharedPreferencesUtils {
    companion object {
        const val accessTokenConst = "access_token"
        const val refreshTokenConst = "refresh_token"
        const val userConst = "user"

        private lateinit var applicationContext: Context

        fun setContext(con: Context) {
            applicationContext = con
        }

        fun storeTokens(accessToken: String, refreshToken: String) {
            val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(accessTokenConst, accessToken)
            editor.putString(refreshTokenConst, refreshToken)
            editor.apply()
        }

        fun clearAllSharedPreferences() {
            applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit().clear().apply()
        }

        fun getAccessToken(): String? {
            val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            return pref.getString(accessTokenConst, null)
        }

        fun getRefreshToken(): String? {
            val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            return pref.getString(refreshTokenConst, null)
        }

        fun isTokenPresent(): Boolean {
            val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            return pref.getString(accessTokenConst, null) != null
        }

    }
}