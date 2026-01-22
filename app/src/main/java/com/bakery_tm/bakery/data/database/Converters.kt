package com.bakery_tm.bakery.data.database

import androidx.room.TypeConverter
import com.bakery_tm.bakery.models.Address
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN))

    @TypeConverter
    fun fromAddress(value: Address?): String? = value?.let { Gson().toJson(it) }

    @TypeConverter
    fun toAddress(value: String?): Address? = value?.let { Gson().fromJson(it, Address::class.java) }

    @TypeConverter
    fun fromAllergens(value: List<String>): String? = Gson().toJson(value)

    @TypeConverter
    fun toAllergens(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    private companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"
    }
}