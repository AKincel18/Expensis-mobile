package pl.polsl.expensis_mobile.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

@SuppressLint("NewApi")
class User {
    @SerializedName("email")
    var email: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("gender")
    var gender: Char = 'F'

    @SerializedName("birth_date")
    var birthDate: LocalDate = LocalDate.now()

    @SerializedName("monthly_limit")
    var monthlyLimit: Double? = 0.0

    @SerializedName("income_range")
    var incomeRange: Int = 0

    override fun toString(): String {
        return "email = $email," +
                "password = $password, " +
                "gender = $gender, " +
                "birthDate = $birthDate, " +
                "monthlyLimit = $monthlyLimit, " +
                "incomeRange = $incomeRange"
    }
}