package pl.polsl.expensis_mobile.models.user

import com.google.gson.annotations.SerializedName
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.utils.Utils

class UserExtension : UserBase {

    @SerializedName("password")
    var password: String = ""

    @SerializedName("username")
    var username: String = ""

    constructor()

    constructor(userDTO: UserFormDTO) {
        email = userDTO.emailInput.text.toString()
        gender = if (userDTO.genderSpinner.selectedItemPosition == 0) 'F' else 'M'
        birthDate = Utils.stringToLocalDate(userDTO.dateInput.text.toString())
        monthlyLimit = if (userDTO.monthlyLimitInput.text.toString().isNotEmpty())
            userDTO.monthlyLimitInput.text.toString().toDouble() else null
        incomeRange = (userDTO.incomeRangesSpinner.selectedItem as IncomeRange).id
        password = userDTO.passwordInput.text.toString()
        username = email
    }

    fun prepareToUpdatingExtension(userDTO: UserFormDTO) {
        super.prepareToUpdatingBase(userDTO)
        password = userDTO.passwordInput.text.toString() //password was changed*/
        username = email
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