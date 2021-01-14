package pl.polsl.expensis_mobile.validators

import pl.polsl.expensis_mobile.dto.stats.StatsRequestFormDTO
import pl.polsl.expensis_mobile.exceptions.MyException
import pl.polsl.expensis_mobile.others.StatsName
import pl.polsl.expensis_mobile.utils.Messages.Companion.NOT_SELECTED_FILTER

class StatsValidator(private val statsRequestFormDTO: StatsRequestFormDTO) {

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
        val selectedStatsName = statsRequestFormDTO.statsNameSpinner.selectedItem as StatsName
        if (selectedStatsName == StatsName.COMBINED || selectedStatsName == StatsName.SEPARATED) {
            if (!statsRequestFormDTO.incomeRangeCheckBox.isChecked &&
                    !statsRequestFormDTO.ageRangeCheckBox.isChecked &&
                    !statsRequestFormDTO.genderCheckBox.isChecked)
                throw MyException(NOT_SELECTED_FILTER)
        }
    }
}