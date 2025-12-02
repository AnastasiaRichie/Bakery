package com.bakery_tm.bakery.data.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN))

    private companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"
    }
}