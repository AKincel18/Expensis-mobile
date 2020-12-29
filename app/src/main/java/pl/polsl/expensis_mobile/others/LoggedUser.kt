package pl.polsl.expensis_mobile.others

import com.google.gson.annotations.SerializedName
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.Utils
import java.time.LocalDate

class LoggedUser {

    @SerializedName("id")
    val id: Int = 0

    @SerializedName("email")
    var email: String? = null

    @SerializedName("gender")
    var gender: Char? = null

    @SerializedName("birth_date")
    var birthDate: LocalDate = LocalDate.now()

    @SerializedName("monthly_limit")
    var monthlyLimit: Double? = null

    @SerializedName("income_range")
    var incomeRange: Int = 0

    /**
     * return logged user as object from shared pref where key is 'user' and value is json string
     *
     */
    fun serialize(): LoggedUser? {
        return Utils.createUserJsonBuilder().fromJson<LoggedUser>(
                SharedPreferencesUtils.getUser(),
                LoggedUser::class.java
        )
    }

    override fun toString(): String {
        return "id = $id, " +
                "email = $email, " +
                "gender = $gender, " +
                "birthDate = $birthDate, " +
                "monthlyLimit = $monthlyLimit, " +
                "incomeRange = $incomeRange"
    }
}