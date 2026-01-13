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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    onBack: () -> Unit = {},
    onGuestClick: () -> Unit = {},
    onRegistrateClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) BackgroundDark else BackgroundLight

    var name by remember { mutableStateOf("Alex") }
    var surname by remember { mutableStateOf("Johnson") }
    var email by remember { mutableStateOf("alex.j@coffee.com") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val emailError = true

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null)
                }
                Spacer(Modifier.weight(1f))
                Text("Регистрация", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(32.dp))
            }
            Text(
                "Присоединиться к Комьюнити",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Text(
                "Заказывайте Ваш любимый кофе и снеки за считанные секунды.",
                color = if (dark) Color(0xFF9CA3AF) else Color(0xFF4B5563),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            InputField("Имя", name) { name = it }
            InputField("Фамилия (опционально)", surname) { surname = it }

            InputField(label = "Почта", value = email, onValueChange = { email = it }, isError = emailError)

            if (emailError) {
                Row(
                    modifier = Modifier.padding(start = 20.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Эта почта уже зарегистрирована. ", color = Color.Red, fontSize = 12.sp)
                    Text(
                        "Войти?",
                        color = Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onLoginClick)
                    )
                }
            }

            PasswordField(
                value = password,
                onValueChange = { password = it },
                show = showPassword,
                onToggle = { showPassword = !showPassword }
            )

            Button(
                onClick = onRegistrateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Зарегистрироваться", color = BackgroundDark, fontSize = 18.sp)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Уже есть аккаунт? Войти",
                    modifier = Modifier.clickable(onClick = onLoginClick),
                    color = if (dark) MutedTextDark else Color.Gray
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text("ИЛИ", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(Modifier.weight(1f))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Primary.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onGuestClick) {
                        Icon(Icons.Default.Person, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Продолжить как Гость")
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}