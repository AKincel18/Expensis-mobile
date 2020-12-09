package pl.polsl.expensis_mobile.models

import com.google.gson.annotations.SerializedName
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.utils.Utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    constructor(userDTO: UserFormDTO) {
        email = userDTO.emailInput.text.toString();
        gender = if(userDTO.genderSpinner.selectedItemPosition == 0) 'F' else 'M'
        birthDate = Utils.stringToLocalDate(userDTO.dateInput.text.toString())
        monthlyLimit = userDTO.monthlyLimitInput.text.toString().toDouble()
        incomeRange = (userDTO.incomeRangesSpinner.selectedItem as IncomeRange).id
        password = userDTO.passwordInput.text.toString();
    }

    override fun toString(): String {
        return "email = $email," +
                "password = $password, " +
                "gender = $gender, " +
                "birthDate = $birthDate, " +
                "monthlyLimit = $monthlyLimit, " +
                "incomeRange = $incomeRange"
    }


}