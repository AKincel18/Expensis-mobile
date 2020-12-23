package pl.polsl.expensis_mobile.rest

import com.android.volley.VolleyError


interface ServerCallback<T> {
    fun onSuccess(response: T)
    fun onFailure(error: VolleyError)
}
