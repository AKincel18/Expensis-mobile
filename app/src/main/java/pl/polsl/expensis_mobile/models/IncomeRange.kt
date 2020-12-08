package pl.polsl.expensis_mobile.models

import com.google.gson.annotations.SerializedName

class IncomeRange(
        private val id: Int,
        @SerializedName("range_from")
        private val rangeFrom: Int,
        @SerializedName("range_to")
        private val rangeTo: Int

) {
    override fun toString(): String {
        return if (id != 0)
            "$rangeFrom-$rangeTo"
        else
            "Select income range"
    }
}
