package pl.polsl.expensis_mobile.others

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

class LoggedUser(
        @SerializedName("id")
        val id: Int,

        @SerializedName("email")
        val email: String,

        @SerializedName("gender")
        val gender: Char,

        @SerializedName("birth_date")
        val birthDate: LocalDate,

        @SerializedName("monthly_limit")
        val monthlyLimit: Double,

        @SerializedName("income_range")
        val incomeRange: Int
) {
    override fun toString(): String {
        return "id = $id, " +
                "email = $email, " +
                "gender = $gender, " +
                "birthDate = $birthDate, " +
                "monthlyLimit = $monthlyLimit, " +
                "incomeRange = $incomeRange"
    }
}