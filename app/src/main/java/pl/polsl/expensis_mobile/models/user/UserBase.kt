package pl.polsl.expensis_mobile.models.user

import com.google.gson.annotations.SerializedName
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.utils.Utils
import java.time.LocalDate

open class UserBase {
    @SerializedName("email")
    var email: String = ""

    @SerializedName("gender")
    var gender: Char = 'F'

    @SerializedName("birth_date")
    var birthDate: LocalDate = LocalDate.now()

    @SerializedName("monthly_limit")
    var monthlyLimit: Double? = 0.0

    @SerializedName("income_range")
    var incomeRange: Int = 0

    fun prepareToUpdatingBase(userDTO: UserFormDTO) {
        email = if (userDTO.emailInput.text.toString().isNotEmpty())
            userDTO.emailInput.text.toString()
        else
            userDTO.emailInput.hint.toString()
        gender = if (userDTO.genderSpinner.selectedItemPosition == 0) 'F' else 'M'
        birthDate = if (userDTO.dateInput.text.toString().isNotEmpty())
            Utils.stringToLocalDate(userDTO.dateInput.text.toString())
        else
            Utils.stringToLocalDate(userDTO.dateInput.hint.toString())
        monthlyLimit = if (userDTO.monthlyLimitInput.text.toString().isNotEmpty()) {
            userDTO.monthlyLimitInput.text.toString().toDouble()
        } else {
            if (userDTO.monthlyLimitInput.hint.toString().isNotEmpty())
                userDTO.monthlyLimitInput.hint.toString().toDouble()
            else
                null
        }
        incomeRange = (userDTO.incomeRangesSpinner.selectedItem as IncomeRange).id
    }
}