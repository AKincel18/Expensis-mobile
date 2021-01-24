package pl.polsl.expensis_mobile.rest

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.rest.requests.CustomArrayRequest
import pl.polsl.expensis_mobile.rest.requests.CustomJsonArrayRequest
import pl.polsl.expensis_mobile.rest.requests.CustomJsonRequest
import pl.polsl.expensis_mobile.rest.requests.CustomStringRequest

class VolleyService {

    fun requestArray(
        method: Int,
        url: String,
        jsonRequest: JSONArray?,
        callback: ServerCallback<JSONArray>,
        context: Context
    ) {
        val objectRequest = CustomArrayRequest(method, url, jsonRequest,
            Response.Listener { response ->
                callback.onSuccess(response)
            },
            Response.ErrorListener { error ->
                callback.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    fun requestObject(
        method: Int,
        url: String,
        jsonRequest: JSONObject?,
        callback: ServerCallback<JSONObject>,
        context: Context
    ) {
        val objectRequest = CustomJsonRequest(method, url, jsonRequest,
            Response.Listener { response ->
                callback.onSuccess(response)
            },
            Response.ErrorListener { error ->
                callback.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    /**
     * request -> jsonObject
     * response -> jsonArray
     */
    fun requestMixed(
        method: Int,
        url: String,
        jsonRequest: JSONObject?,
        callback: ServerCallback<JSONArray>,
        context: Context
    ) {
        val objectRequest = CustomJsonArrayRequest(method, url, jsonRequest,
            Response.Listener { response ->
                callback.onSuccess(response!!)
            },
            Response.ErrorListener { error ->
                callback.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)

    }

    fun requestObjectNoAuth(
        method: Int,
        url: String,
        jsonRequest: JSONObject?,
        callback: ServerCallback<JSONObject>,
        context: Context
    ) {
        val objectRequest = JsonObjectRequest(method, url, jsonRequest,
            { response ->
                callback.onSuccess(response)
            },
            { error ->
                callback.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    fun requestString(
        method: Int,
        url: String,
        callback: ServerCallback<String>,
        context: Context
    ) {
        val stringRequest = CustomStringRequest(method, url,
            Response.Listener { response ->
                callback.onSuccess(response)
            },
            Response.ErrorListener { error ->
                callback.onFailure(error)
            })
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

}
