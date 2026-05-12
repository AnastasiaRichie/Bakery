package com.bakery_tm.bakery.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

fun dateFormatter(millis: Long, format: String = "dd.MM.yyyy HH:mm"): String? {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getGreeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Доброе утро!"
        in 12..16 -> "Добрый день!"
        in 17..21 -> "Добрый вечер!"
        else -> "Доброй ночи!"
    }
}

inline fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline collector: suspend CoroutineScope.(T) -> Unit
) = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.lifecycle.repeatOnLifecycle(state) {
        collect {
            collector(it)
        }
    }
}