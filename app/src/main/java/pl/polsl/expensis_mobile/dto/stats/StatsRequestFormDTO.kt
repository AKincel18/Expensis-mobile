package pl.polsl.expensis_mobile.dto.stats

import android.widget.CheckBox
import android.widget.Spinner

class StatsRequestFormDTO(
        val statsNameSpinner: Spinner,
        val incomeRangeCheckBox: CheckBox,
        val ageRangeCheckBox: CheckBox,
        val genderCheckBox: CheckBox
)
