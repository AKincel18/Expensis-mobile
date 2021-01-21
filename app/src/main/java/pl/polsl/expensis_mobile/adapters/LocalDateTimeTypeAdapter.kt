package pl.polsl.expensis_mobile.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        out?.value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value))
    }

    override fun read(input: JsonReader?): LocalDateTime {
        return LocalDateTime.parse(
            input?.nextString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        )
    }
}