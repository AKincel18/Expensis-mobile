package pl.polsl.expensis_mobile.dto.stats

import com.google.gson.annotations.SerializedName

class StatResponseDTO(

        @SerializedName("name_value")
        val nameValue: String,

        @SerializedName("user_value")
        val userValue: Float,

        @SerializedName("all_value")
        val allValue: Float
)