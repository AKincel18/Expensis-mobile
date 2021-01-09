package pl.polsl.expensis_mobile.utils

import com.google.gson.Gson
import pl.polsl.expensis_mobile.adapters.LocalDateTypeAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Utils {
    companion object {
        fun parseDateToString(year: Int, _month: Int, day: Int): String {
            val month = _month + 1
            val monthString: String = if (month >= 10) month.toString() else "0$month"
            val dayString: String = if (day >= 10) day.toString() else "0$day"
            return "$year-$monthString-$dayString"
        }

        fun parseFullDateToString(date: LocalDate): String {
            return parseDateToString(date.year, date.monthValue - 1, date.dayOfMonth)
        }

        @Throws(DateTimeParseException::class)
        fun stringToLocalDate(date: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(date, formatter)
        }

        fun getGsonWithLocalDate(): Gson {
            return Gson()
                .newBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
                .create()
        }
    }
}
