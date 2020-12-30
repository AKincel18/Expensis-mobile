package pl.polsl.expensis_mobile.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Expense(
    var id: Int,
    @SerializedName("user")
    var userId: Int,
    var date: LocalDateTime,
    var title: String,
    var description: String?,
    var category: String,
    var value: Double
)