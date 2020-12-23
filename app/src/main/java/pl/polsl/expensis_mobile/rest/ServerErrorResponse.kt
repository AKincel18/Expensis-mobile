package pl.polsl.expensis_mobile.rest

import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import pl.polsl.expensis_mobile.utils.Messages

class ServerErrorResponse(private var error: VolleyError) {

    fun getErrorResponse(): String? {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            val data = String(error.networkResponse.data, charset("UTF-8"))
            val jsonObject = JsonParser.parseString(data).asJsonObject
            val statusCode = error.networkResponse.statusCode
            if (statusCode == 500)
                return Messages.UNEXPECTED_ERROR
            return getFirstError(jsonObject)
        }
        if (error is NoConnectionError || error is TimeoutError)
            return Messages.NO_SERVER_CONNECTION
        return error.toString()
    }

    private fun getFirstError(jsonObject: JsonObject): String? {
        for ((key, value) in jsonObject.entrySet()) {

            if (value is JsonArray) {
                if (value.size() > 0) {
                    return value.get(0).asString
                }
            } else if (value is JsonPrimitive) {
                return jsonObject.get(key).asString
            }


        }
        return null
    }

}