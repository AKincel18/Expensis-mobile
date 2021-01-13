package pl.polsl.expensis_mobile.others

enum class StatName(private val statName: String) {
    CATEGORIES("Categories"),
    COMBINED("Combined"),
    SEPARATED("Separated");

    override fun toString(): String {
        return statName
    }
}