package pl.polsl.expensis_mobile.utils

class Utils {
    companion object {
        fun parseDateToString(year: Int, _month: Int, day: Int): String {
            val month = _month + 1
            val monthString: String = if (month >= 10) month.toString() else "0$month"
            val dayString: String = if (day >= 10) day.toString() else "0$day"
            return "$dayString/$monthString/$year"
        }
    }
}