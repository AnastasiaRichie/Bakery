package com.bakery_tm.bakery.screen

import androidx.annotation.DrawableRes
import com.bakery_tm.bakery.R

enum class ProfileAvatar(@DrawableRes val iconRes: Int) {
    Calm(R.drawable.sentiment_calm),
    Excited(R.drawable.sentiment_excited),
    Satisfied(R.drawable.sentiment_satisfied),
    VerySatisfied(R.drawable.sentiment_very_satisfied)
}