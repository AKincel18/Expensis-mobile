package pl.polsl.expensis_mobile.models

class Category(
    var id: Int,
    var value: String
) {
    override fun toString(): String {
        return value
    }
}