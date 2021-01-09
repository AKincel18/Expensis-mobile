package pl.polsl.expensis_mobile.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class Expense(
    var id: Int,
    @SerializedName("user")
    var userId: Int,
    var date: LocalDate,
    var title: String,
    var description: String?,
    var category: String,
    var value: Double
) : Serializable