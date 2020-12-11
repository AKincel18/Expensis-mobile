package pl.polsl.expensis_mobile.dto

import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

data class UserFormDTO(
    val emailInput: EditText,
    val genderSpinner: Spinner,
    val dateInput: TextView,
    val monthlyLimitInput: EditText,
    val incomeRangesSpinner: Spinner,
    val passwordInput: EditText,
    val passwordConfirmInput: EditText,
    val genderHint: String
)