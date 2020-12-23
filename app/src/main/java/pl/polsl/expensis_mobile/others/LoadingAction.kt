package pl.polsl.expensis_mobile.others

interface LoadingAction {
    fun showProgressBar()
    fun changeEditableFields(isEnabled: Boolean)
}