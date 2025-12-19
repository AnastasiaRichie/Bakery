package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnregisteredScreenUi(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            imageVector = Icons.Default.AccountBox,
            modifier = Modifier.width(192.dp).height(192.dp),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "Войдите или зарегистрируйтесь для заказа товаров",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}
