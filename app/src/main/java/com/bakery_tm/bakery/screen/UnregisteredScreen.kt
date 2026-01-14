package com.bakery_tm.bakery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UnregisteredScreenUi(modifier: Modifier, onLoginClicked: () -> Unit) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    Box(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderHistoryTopBar()
            GuestBanner(onLoginClick = onLoginClicked)
        }
    }

//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Image(
//            imageVector = Icons.Default.AccountBox,
//            modifier = Modifier.width(192.dp).height(192.dp),
//            contentDescription = null
//        )
//        Spacer(modifier = Modifier.height(36.dp))
//        Text(
//            modifier = Modifier.padding(horizontal = 20.dp),
//            text = "Войдите или зарегистрируйтесь для заказа товаров",
//            fontSize = 20.sp,
//            textAlign = TextAlign.Center
//        )
//    }
}
