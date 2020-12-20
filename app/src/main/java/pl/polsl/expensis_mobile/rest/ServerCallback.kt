package pl.polsl.expensis_mobile.rest

import org.json.JSONArray


interface ServerCallback {
        fun onSuccess(result: JSONArray?)
    }
