package com.bakery_tm.bakery.data.database

import androidx.room.TypeConverter
import com.bakery_tm.bakery.models.Address
import com.google.gson.Gson
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

    private companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"
    }
}