package pl.polsl.expensis_mobile.dto.stats

import android.widget.CheckBox
import android.widget.Spinner

class StatRequestFormDTO(
        val statNameSpinner: Spinner,
        val incomeRangeCheckBox: CheckBox,
        val ageRangeCheckBox: CheckBox,
        val genderCheckBox: CheckBox
)
