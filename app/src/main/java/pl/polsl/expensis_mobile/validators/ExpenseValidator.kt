package pl.polsl.expensis_mobile.validators

import pl.polsl.expensis_mobile.dto.ExpenseFormDTO
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.models.Category
import pl.polsl.expensis_mobile.utils.Messages
import pl.polsl.expensis_mobile.utils.Utils
import java.time.format.DateTimeParseException

class ExpenseValidator(private val expenseDTO: ExpenseFormDTO) {
    fun validateAddExpenseAction(): ValidationResult {
        try {
            validateTitle()
            validateDate()
            validateCategory()
            validateValue()
        } catch (e: MyException) {
            return ValidationResult(false, e.message!!)
        } catch (e: DateTimeParseException) {
            return ValidationResult(false, Messages.WRONG_DATE_ERROR)
        } catch (e: NumberFormatException) {
            return ValidationResult(false, Messages.WRONG_VALUE_ERROR)
        }
        return ValidationResult(true, Messages.SUCCESSFULLY_CREATED)
    }

    @Throws(MyException::class)
    private fun validateTitle() {
        if (expenseDTO.titleInput.text.toString().isEmpty())
            throw MyException(Messages.EMPTY_TITLE)
    }

    @Throws(DateTimeParseException::class)
    private fun validateDate() {
        Utils.stringToLocalDate(expenseDTO.creationDateInput.text.toString())
    }

    @Throws(MyException::class)
    private fun validateCategory() {
        val selectedItem = expenseDTO.categorySpinner.selectedItem as Category
        if (selectedItem.id == 0) {
            throw MyException(Messages.NOT_SELECTED_CATEGORY)
        }
    }

    @Throws(NumberFormatException::class, MyException::class)
    private fun validateValue() {
        val value = expenseDTO.valueInput.text.toString()
        if (value.isEmpty()) {
            throw MyException(Messages.EMPTY_VALUE)
        }
        value.toDouble()
    }
}