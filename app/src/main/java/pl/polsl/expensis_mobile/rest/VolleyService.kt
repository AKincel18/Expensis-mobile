package pl.polsl.expensis_mobile.rest

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

class VolleyService() {

    private var callbackArray: ServerCallback<JSONArray>? = null
    private var callbackObject: ServerCallback<JSONObject>? = null
    lateinit var context: Context

    fun requestArray(method: Int, url: String, jsonRequest: JSONArray?) {
        val objectRequest = JsonArrayRequest(method, url, jsonRequest,
                { response ->
                    callbackArray?.onSuccess(response)
                },
                { error ->
                    callbackArray?.onFailure(error)
                }
        )
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest)
    }

    fun requestObject(method: Int, url: String, jsonRequest: JSONObject?) {
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
