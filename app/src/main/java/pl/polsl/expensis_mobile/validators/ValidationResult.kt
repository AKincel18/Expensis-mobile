package pl.polsl.expensis_mobile.validators

data class ValidationResult(
    var isValid: Boolean,
    var message: String?
) {
    var extraMessage: String? = null

    constructor(isValid: Boolean, message: String?, extraMessage: String) : this(isValid, message){
        this.extraMessage = extraMessage
    }
}