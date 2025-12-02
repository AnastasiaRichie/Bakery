package com.bakery_tm.bakery.common

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)


    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, true)

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_IS_LOGGED_IN, loggedIn) }
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}