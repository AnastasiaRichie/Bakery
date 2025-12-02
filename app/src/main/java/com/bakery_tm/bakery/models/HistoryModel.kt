package com.bakery_tm.bakery.models

data class HistoryModel(
    val date: String,
    val time: String,
    val city: String,
    val street: String,
    val description: String,
    val sum: String
)
