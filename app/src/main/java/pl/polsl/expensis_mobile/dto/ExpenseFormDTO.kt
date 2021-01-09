package pl.polsl.expensis_mobile.dto

import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

data class ExpenseFormDTO(
    val titleInput: EditText,
    val descriptionInput: EditText,
    val creationDateInput: TextView,
    val categorySpinner: Spinner,
    val valueInput: EditText
)