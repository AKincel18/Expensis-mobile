package pl.polsl.expensis_mobile.dto

import pl.polsl.expensis_mobile.models.Category
import pl.polsl.expensis_mobile.utils.Utils
import java.time.LocalDate

class ExpenseDTO(expenseFormDTO: ExpenseFormDTO) {
    private var title: String = ""
    private var description: String = ""
    private var date: LocalDate = LocalDate.now()
    private var category: Int = 0
    private var value: Double = 0.0

    init {
        title = expenseFormDTO.titleInput.text.toString()
        description = expenseFormDTO.descriptionInput.text.toString()
        date = Utils.stringToLocalDate(expenseFormDTO.creationDateInput.text.toString())
        category = (expenseFormDTO.categorySpinner.selectedItem as Category).id
        value = expenseFormDTO.valueInput.text.toString().toDouble()
    }
}