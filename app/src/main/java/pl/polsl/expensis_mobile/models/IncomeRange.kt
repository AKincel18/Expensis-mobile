package pl.polsl.expensis_mobile.models

import com.google.gson.annotations.SerializedName

class IncomeRange(
    @SerializedName("id")
    var id: Int,
    @SerializedName("range_from")
    private var rangeFrom: Int,
    @SerializedName("range_to")
    private var rangeTo: Int
) {

    override fun toString(): String {
        return if (id != 0)
            "$rangeFrom-$rangeTo"
        else
            "Select income range*"
    }
}
