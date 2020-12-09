package pl.polsl.expensis_mobile.validators

import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.models.User
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_EQUAL_PASSWORDS_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_GENDER_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_INCOME_RANGE_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_STRONG_PASSWORD_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.SUCCESSFULLY_REGISTERED
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_DATE_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_EMAIL_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_MONTHLY_LIMIT_ERROR
import pl.polsl.expensis_mobile.utils.Utils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.regex.Pattern

class UserValidator(
        private val emailInput: EditText,
        private val genderSpinner: Spinner,
        private val dateInput: TextView,
        private val monthlyLimitInput: EditText,
        private val incomeRangesSpinner: Spinner,
        private val passwordInput: EditText,
        private val passwordConfirmInput: EditText,
        private val genderHint: String) {

    var messageValidation = ""

    fun validateUser(): User? {

        val user: User? = User()
        try {
            user!!.email = validateEmail()
            user.gender = validateGender()
            user.birthDate = validateDate()!!
            user.monthlyLimit = validateMonthlyLimit()
            user.incomeRange = validateIncomeRange()
            user.password = validatePassword()
        } catch (e: MyException) {
            messageValidation = e.message!!
            return null
        } catch (e: ParseException) {
            messageValidation = WRONG_DATE_ERROR
            return null
        } catch (e: NumberFormatException) {
            messageValidation = WRONG_MONTHLY_LIMIT_ERROR
            return null
        }
        messageValidation = SUCCESSFULLY_REGISTERED
        return user
    }

    @Throws(MyException::class)
    private fun validateEmail(): String {
        val email = emailInput.text.toString()
        if (!isEmailValid(email))
            throw MyException(WRONG_EMAIL_ERROR)
        return email
    }

    @Throws(MyException::class)
    private fun validateGender(): Char {
        if (genderSpinner.selectedItem.toString() == genderHint)
            throw MyException(NOT_SELECTED_GENDER_ERROR)
        return if (genderSpinner.selectedItemPosition == 0) {
            'F'
        } else {
            'M'
        }
    }

    @Throws(ParseException::class)
    private fun validateDate(): LocalDate? {
        val format: DateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

        val dateWithoutTime: LocalDate?
        val date: Date? = format.parse(dateInput.text.toString())
        val calendar = Utils.dateToCalendar(date!!)

        dateWithoutTime = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        return dateWithoutTime
    }

    @Throws(NumberFormatException::class)
    private fun validateMonthlyLimit(): Double? {
        val monthlyLimit = monthlyLimitInput.text.toString()
        if (monthlyLimit.isEmpty())
            return null
        return monthlyLimit.toDouble()
    }

    @Throws(MyException::class)
    private fun validateIncomeRange(): Int {
        val selectedItem = incomeRangesSpinner.selectedItem as IncomeRange
        if (selectedItem.id == 0) {
            throw MyException(NOT_SELECTED_INCOME_RANGE_ERROR)
        }
        return selectedItem.id
    }

    @Throws(MyException::class)
    private fun validatePassword(): String {
        val password = passwordInput.text.toString()
        val passwordConfirm = passwordConfirmInput.text.toString()

        if (!isPasswordValid(password))
            throw MyException(NOT_STRONG_PASSWORD_ERROR)
        if (password != passwordConfirm)
            throw MyException(NOT_EQUAL_PASSWORDS_ERROR)

        return passwordInput.text.toString()
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return Pattern.compile(
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&-_,.])[A-Za-z\\d@\$!%*#?&-_,.]{5,64}"
        ).matcher(password).matches()

    }
}