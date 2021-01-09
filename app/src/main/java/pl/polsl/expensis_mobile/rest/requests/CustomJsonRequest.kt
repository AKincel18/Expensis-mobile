package pl.polsl.expensis_mobile.rest.requests

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils


class CustomJsonRequest(
    method: Int,
    url: String,
    jsonRequest: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(method, url, jsonRequest, listener, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        return if (response?.data == null || response.data.isEmpty()) {
            Response.success(JSONObject(), HttpHeaderParser.parseCacheHeaders(response))
        } else {
            super.parseNetworkResponse(response);
        }
    }

    override fun getHeaders(): MutableMap<String, String> {
        if (SharedPreferencesUtils.isTokenPresent()) {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer " + SharedPreferencesUtils.getAccessToken()
            return headers
        }
        return super.getHeaders()
    }
}