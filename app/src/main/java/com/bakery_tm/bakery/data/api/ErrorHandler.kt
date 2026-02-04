package com.bakery_tm.bakery.data.api

import com.google.gson.Gson
import retrofit2.HttpException

class ErrorHandler(
    private val gson: Gson
) {

    fun parseError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val error = gson.fromJson(errorBody, ErrorResponse::class.java)
            error.message
        } catch (ex: Exception) {
            "Произошла ошибка, попробуйте позже"
        }
    }
}