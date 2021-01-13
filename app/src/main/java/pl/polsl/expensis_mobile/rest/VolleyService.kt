package pl.polsl.expensis_mobile.rest

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.rest.requests.CustomArrayRequest
import pl.polsl.expensis_mobile.rest.requests.CustomJsonArrayRequest
import pl.polsl.expensis_mobile.rest.requests.CustomJsonRequest

class VolleyService() {

    private var callbackArray: ServerCallback<JSONArray>? = null
    private var callbackObject: ServerCallback<JSONObject>? = null
    lateinit var context: Context

    fun requestArray(method: Int, url: String, jsonRequest: JSONArray?) {
        val objectRequest = CustomArrayRequest(method, url, jsonRequest,
            Response.Listener { response ->
                callbackArray?.onSuccess(response)
            },
            Response.ErrorListener { error ->
                callbackArray?.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    fun requestObject(method: Int, url: String, jsonRequest: JSONObject?) {
        val objectRequest = CustomJsonRequest(method, url, jsonRequest,
            Response.Listener { response ->
                callbackObject?.onSuccess(response)
            },
            Response.ErrorListener { error ->
                callbackObject?.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    /**
     * request -> jsonObject
     * response -> jsonArray
     */
    fun requestMixed(method: Int, url: String, jsonRequest: JSONObject?) {
        val objectRequest = CustomJsonArrayRequest(method, url, jsonRequest,
                Response.Listener {
                    response -> callbackArray?.onSuccess(response!!)
                },
                Response.ErrorListener {
                    error -> callbackArray?.onFailure(error)
                }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)

    }

    fun requestObjectNoAuth(method: Int, url: String, jsonRequest: JSONObject?) {
        val objectRequest = JsonObjectRequest(method, url, jsonRequest,
            { response ->
                callbackObject?.onSuccess(response)
            },
            { error ->
                callbackObject?.onFailure(error)
            }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }


    constructor(callback: ServerCallback<JSONArray>, context: Context) : this() {
        this.callbackArray = callback
        this.context = context
    }

    constructor(context: Context, callback: ServerCallback<JSONObject>) : this() {
        this.context = context
        this.callbackObject = callback
    }
}
