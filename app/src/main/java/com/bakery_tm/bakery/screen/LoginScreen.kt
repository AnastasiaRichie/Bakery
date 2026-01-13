package com.bakery_tm.bakery.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    modifier: Modifier,
    onFoodNavigation: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClicked: () -> Unit,
) {
    val darkTheme = isSystemInDarkTheme()
    val bg = if (darkTheme) BackgroundDark else BackgroundLight

    var email by remember { mutableStateOf("alex@example.com") }
    var password by remember { mutableStateOf("password123") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(390.dp)
                .height(844.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(bg)
            ) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(Modifier.weight(1f))
//                    Text("Login", style = MaterialTheme.typography.titleLarge)
//                    Spacer(Modifier.weight(1f))
//                }

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Primary, RoundedCornerShape(20.dp))
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO (tea cup icon)
                    Icon(Icons.Default.Home, null, tint = BackgroundDark, modifier = Modifier.size(40.dp))
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Зарядись энергией на весь день",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                )

                Text(
                    "Войдите, чтобы заказывать Ваши любимые снэки.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (darkTheme) MutedTextDark else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                )

                if (error) {
                    ErrorBox("Проверьте корректность введенных данных.")
                }

                Spacer(Modifier.height(8.dp))

                InputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )

                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    show = showPassword,
                    onToggle = { showPassword = !showPassword }
                )

                TextButton(
                    onClick = onForgotClicked,
                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
                ) {
                    Text("Забыли пароль?", color = Primary)
                }
                PrimaryButton("Войти", onFoodNavigation)
                OutlinedButton(
                    onClick = onFoodNavigation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                ) { Text("Продолжить как Гость") }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                ) {
                    Text("Нет аккаунта?")
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Зарегистрироваться",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSignUpClick).padding(bottom = 8.dp)
                    )
                }

                //TODO (Добавить Правила использования)
            }
        }
    }
}

@Composable
fun ErrorBox(text: String) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .background(Color(0x1AFF0000), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.Red)
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit, show: Boolean, onToggle: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text("Пароль", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(if (show) Icons.Default.Check else Icons.Default.Clear, null)
                }
            }
        )
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 4.dp, bottom = 16.dp)
            .height(56.dp)
    ) {
        Text(text, color = BackgroundDark, fontSize = 18.sp)
    }
}

//@Composable
//fun SecurityFooter() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
//            Spacer(Modifier.width(4.dp))
//            Text("SECURE CONNECTION", fontSize = 10.sp, letterSpacing = 1.sp)
//        }
//        Text(
//            "Your connection to our servers is encrypted. We value your privacy and protect your data according to global security standards.",
//            fontSize = 12.sp,
//            textAlign = TextAlign.Center,
//            color = Color.Gray,
//            modifier = Modifier.padding(top = 4.dp)
//        )
//    }
//}

val Primary = Color(0xFF2BEE6C)
val BackgroundLight = Color(0xFFF6F8F6)
val BackgroundDark = Color(0xFF102216)
val InputDark = Color(0xFF193322)
val BorderDark = Color(0xFF326744)
val MutedTextDark = Color(0xFF92C9A4)