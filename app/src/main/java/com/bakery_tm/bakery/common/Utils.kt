package com.bakery_tm.bakery.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateFormatter(millis: Long): String? {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}