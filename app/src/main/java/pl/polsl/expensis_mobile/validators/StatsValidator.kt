package pl.polsl.expensis_mobile.validators

import pl.polsl.expensis_mobile.dto.stats.StatRequestFormDTO
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.others.StatName
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_FILTER

class StatsValidator(private val statRequestFormDTO: StatRequestFormDTO) {

    fun validateStats(): ValidationResult {
        try {
            validateFilters()
        } catch (e: MyException) {
            return ValidationResult(false, e.message)
        }
        return ValidationResult(true, null)
    }

    @Throws(MyException::class)
    private fun validateFilters() {
        val selectedStatName = statRequestFormDTO.statNameSpinner.selectedItem as StatName
        if (selectedStatName == StatName.COMBINED || selectedStatName == StatName.SEPARATED) {
            if (!statRequestFormDTO.incomeRangeCheckBox.isChecked &&
                    !statRequestFormDTO.ageRangeCheckBox.isChecked &&
                    !statRequestFormDTO.genderCheckBox.isChecked)
                throw MyException(NOT_SELECTED_FILTER)
        }
    }
}