package pl.polsl.expensis_mobile.validators

import pl.polsl.expensis_mobile.dto.LoginFormDTO
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.utils.Messages

class LoginValidator {
    lateinit var loginDTO: LoginFormDTO

    fun validate(login: LoginFormDTO): ValidationResult {
        this.loginDTO = login
        try {
            validateEmail()
            validatePassword()
        } catch (e: MyException) {
            return ValidationResult(false, e.message!!)
        }
        return ValidationResult(true, null)
    }

    @Throws(MyException::class)
    private fun validateEmail() {
        if (loginDTO.emailInput.text.toString().isEmpty())
            throw MyException(Messages.EMPTY_EMAIL)
    }

    @Throws(MyException::class)
    private fun validatePassword() {
        if (loginDTO.passwordInput.text.toString().isEmpty())
            throw MyException(Messages.EMPTY_PASSWORD)
    }


}