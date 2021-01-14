package pl.polsl.expensis_mobile.dto.stats

import com.google.gson.annotations.SerializedName

class StatsFilterDTO(
        @SerializedName("income_range")
        val incomeRange: Boolean,

        @SerializedName("age_range")
        val ageRange: Boolean,

        @SerializedName("gender")
        val gender: Boolean
)