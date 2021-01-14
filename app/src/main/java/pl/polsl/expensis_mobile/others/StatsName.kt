package pl.polsl.expensis_mobile.others

enum class StatsName(private val statsName: String) {
    CATEGORIES("Categories"),
    COMBINED("Combined"),
    SEPARATED("Separated");

    override fun toString(): String {
        return statsName
    }
}