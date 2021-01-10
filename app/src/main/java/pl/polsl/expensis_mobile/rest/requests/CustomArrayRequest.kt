package pl.polsl.expensis_mobile.rest.requests

import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils

class CustomArrayRequest(
    method: Int,
    url: String,
    jsonRequest: JSONArray?,
    listener: Response.Listener<JSONArray>,
    errorListener: Response.ErrorListener
) : JsonArrayRequest(method, url, jsonRequest, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        if (SharedPreferencesUtils.isTokenPresent()) {
            headers["Authorization"] = "Bearer " + SharedPreferencesUtils.getAccessToken()
        }
        return headers
    }
}