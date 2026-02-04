package com.bakery_tm.bakery.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateFormatter(millis: Long, format: String = "dd.MM.yyyy HH:mm"): String? {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(Date(millis))
}