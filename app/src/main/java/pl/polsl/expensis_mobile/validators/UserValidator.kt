package pl.polsl.expensis_mobile.validators

import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_EQUAL_PASSWORDS_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_GENDER_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_INCOME_RANGE_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_STRONG_PASSWORD_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.SUCCESSFULLY_REGISTERED
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_DATE_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_EMAIL_ERROR
import pl.polsl.expensis_mobile.utils.Messages.Companion.WRONG_MONTHLY_LIMIT_ERROR
import pl.polsl.expensis_mobile.utils.Utils.Companion.stringToLocalDate
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

class UserValidator {

    lateinit var userDTO: UserFormDTO

    fun validate(user: UserFormDTO): ValidationResult {
        this.userDTO = user
        try {
            validateEmail()
            validateGender()
            validateDate()
            validateMonthlyLimit()
            validateIncomeRange()
            validatePassword()
        } catch (e: MyException) {
            return ValidationResult(false, e.message!!)
        } catch (e: DateTimeParseException) {
            return ValidationResult(false, WRONG_DATE_ERROR)
        } catch (e: NumberFormatException) {
            return ValidationResult(false, WRONG_MONTHLY_LIMIT_ERROR)
        }
        return ValidationResult(true, SUCCESSFULLY_REGISTERED)
    }

    @Throws(MyException::class)
    private fun validateEmail() {
        val email = userDTO.emailInput.text.toString()
        if (!isEmailValid(email))
            throw MyException(WRONG_EMAIL_ERROR)
    }

    @Throws(MyException::class)
    private fun validateGender() {
        if (userDTO.genderSpinner.selectedItem.toString() == userDTO.genderHint)
            throw MyException(NOT_SELECTED_GENDER_ERROR)
    }

    @Throws(DateTimeParseException::class)
    private fun validateDate() {
        stringToLocalDate(userDTO.dateInput.text.toString())
    }

    @Throws(NumberFormatException::class)
    private fun validateMonthlyLimit() {
        val monthlyLimit = userDTO.monthlyLimitInput.text.toString()
        if (monthlyLimit.isNotEmpty())
            monthlyLimit.toDouble()
    }

    @Throws(MyException::class)
    private fun validateIncomeRange() {
        val selectedItem = userDTO.incomeRangesSpinner.selectedItem as IncomeRange
        if (selectedItem.id == 0) {
            throw MyException(NOT_SELECTED_INCOME_RANGE_ERROR)
        }
    }

    @Throws(MyException::class)
    private fun validatePassword() {
        val password = userDTO.passwordInput.text.toString()
        val passwordConfirm = userDTO.passwordConfirmInput.text.toString()

        if (!isPasswordValid(password))
            throw MyException(NOT_STRONG_PASSWORD_ERROR)
        if (password != passwordConfirm)
            throw MyException(NOT_EQUAL_PASSWORDS_ERROR)

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