package pl.polsl.expensis_mobile.rest

import com.android.volley.VolleyError
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
        return null
    }

    private fun getFirstError(jsonObject: JsonObject): String? {

        for ((_, value) in jsonObject.entrySet()) {
            val array: JsonArray = value.asJsonArray
            if (array.size() > 0) {
                return array.get(0).asString
            }
        }
        return null
    }

}