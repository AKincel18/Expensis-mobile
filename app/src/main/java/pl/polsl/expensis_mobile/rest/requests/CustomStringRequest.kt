package pl.polsl.expensis_mobile.rest.requests

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils

class CustomStringRequest(
    method: Int,
    url: String?,
    listener: Response.Listener<String>?,
    errorListener: Response.ErrorListener?
) : StringRequest(method, url, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        if (SharedPreferencesUtils.isTokenPresent()) {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer " + SharedPreferencesUtils.getAccessToken()
            return headers
        }
        return super.getHeaders()
    }
}