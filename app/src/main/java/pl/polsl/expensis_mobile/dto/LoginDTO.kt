package pl.polsl.expensis_mobile.dto

class LoginDTO(loginFormDTO: LoginFormDTO) {

    private var email: String = ""

    private var password: String = ""

    init {
        email = loginFormDTO.emailInput.text.toString()
        password = loginFormDTO.passwordInput.text.toString()
    }
}