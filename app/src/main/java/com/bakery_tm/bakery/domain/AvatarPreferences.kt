package com.bakery_tm.bakery.domain

import android.content.Context
import androidx.core.content.edit

class AvatarPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("avatar_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_AVATAR = "selected_avatar"
    }

    fun saveAvatar(avatarName: String) {
        prefs.edit { putString(KEY_SELECTED_AVATAR, avatarName) }
    }

    fun getAvatar(): String? {
        return prefs.getString(KEY_SELECTED_AVATAR, null)
    }
}