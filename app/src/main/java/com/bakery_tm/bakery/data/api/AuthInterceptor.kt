package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.common.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val authManager: AuthManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = runBlocking { authManager.getToken() }
        if (token == null || !authManager.isTokenValid()) { throw NoAuthException() }
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}

class NoAuthException : IOException()
