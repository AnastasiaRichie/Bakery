package com.bakery_tm.bakery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight

@Composable
fun UnregisteredScreenUi(modifier: Modifier, darkTheme: Boolean, onLoginClicked: () -> Unit) {
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    Box(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderHistoryTopBar()
            GuestBanner(onLoginClick = onLoginClicked)
        }
    }
}
