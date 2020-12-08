package pl.polsl.expensis_mobile.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Utils {
    companion object {
        fun parseDateToString(year: Int, _month: Int, day: Int): String {
            val month = _month + 1
            val monthString: String = if (month >= 10) month.toString() else "0$month"
            val dayString: String = if (day >= 10) day.toString() else "0$day"
            return "$dayString/$monthString/$year"
        }

        @Throws(DateTimeParseException::class)
        fun stringToLocalDate(date: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return LocalDate.parse(date, formatter)
        }
    }
}
