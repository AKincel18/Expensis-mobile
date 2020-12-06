package pl.polsl.expensis_mobile.adapters

import android.content.Context
import android.widget.ArrayAdapter

open class SpinnerAdapter<T>(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>) :
    ArrayAdapter<T>(context, resource, textViewResourceId ,objects) {

    override fun getCount(): Int {
        return super.getCount() - 1
    }

}