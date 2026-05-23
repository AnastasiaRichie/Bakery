package com.bakery_tm.bakery.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight
import com.bakery_tm.bakery.common.MutedTextDark
import com.bakery_tm.bakery.common.Primary
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    viewModel: RegistrationViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    orderViewModel: OrderViewModel,
    darkTheme: Boolean,
    isConnected: Boolean,
    onLoginClick: () -> Unit,
    onSuccessClick: () -> Unit,
) {
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var error by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateToFood -> {
                    shoppingCartViewModel.getShoppingCart()
                    orderViewModel.getOrders()
                    onSuccessClick()
                }
                NavigationEvent.NavigateToRegister -> onSuccessClick()
                is NavigationEvent.ShowError -> {
                    error = event.message
                    println("Ошибка: ${event.message}")
                }
                else -> Unit
            }
        }
    }

    RegistrationScreenUi(
        modifier = modifier,
        userStateModel = state,
        error = error,
        background = background,
        isConnected = isConnected,
        darkTheme = darkTheme,
        onRegisterClick = viewModel::onRegisterClick,
        onTermsClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://axiomabio.com/pdf/test.pdf".toUri()
            }
            context.startActivity(intent)
        },
        onPrivacyClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://www.orimi.com/pdf-test.pdf".toUri()
            }
            context.startActivity(intent)
        },
        onNameChanged = { value -> viewModel.onRegistrationValueChanged(FieldType.NAME, value) },
        onSurnameChanged = { value -> viewModel.onRegistrationValueChanged(FieldType.SURNAME, value) },
        onEmailChanged = { value -> viewModel.onRegistrationValueChanged(FieldType.EMAIL, value) },
        onPasswordChanged = { value -> viewModel.onRegistrationValueChanged(FieldType.PASSWORD, value) },
        onGuestClick = viewModel::onGuestClick,
        onLoginClick = onLoginClick,
        showPassword = showPassword,
        onShowChanged = { showPassword = it},
    )
}

@Composable
fun RegistrationScreenUi(
    modifier: Modifier,
    userStateModel: UserStateModel,
    error: String,
    background: Color,
    isConnected: Boolean,
    darkTheme: Boolean,
    onRegisterClick: (UserStateModel) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSurnameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit,
    showPassword: Boolean,
    onShowChanged: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(modifier = modifier.fillMaxSize().background(background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            if (!isConnected) {
                Text(
                    "Проверьте подключение к интернету",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red),
                    color = Color.White
                )
            }
            Text(
                "Присоединиться к Комьюнити",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Text(
                "Заказывайте Ваш любимый кофе и снеки за считанные секунды.",
                color = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            InputField("Имя", userStateModel.name) { onNameChanged(it) }
            InputField(
                "Фамилия (опционально)",
                userStateModel.lastName.orEmpty()
            ) { onSurnameChanged(it) }

            InputField(
                label = "Почта",
                value = userStateModel.email,
                onValueChange = { onEmailChanged(it) },
            )

            PasswordField(
                value = userStateModel.password,
                onValueChange = { onPasswordChanged(it) },
                show = showPassword,
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                onToggle = { onShowChanged(!showPassword) }
            )

            if (error.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$error. ", color = Color.Red, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(
                        "Войти?",
                        color = Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onLoginClick)
                    )
                }
            }

            Button(
                onClick = { onRegisterClick(userStateModel) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Зарегистрироваться", color = BackgroundLight, fontSize = 18.sp)
            }

            val annotatedString = buildAnnotatedString {
                append("Нажимая Зарегистрироваться, Вы принимаете ")
                pushStringAnnotation(tag = "terms", annotation = "terms")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Условия использования")
                }
                pop()
                append(" и ")
                pushStringAnnotation(tag = "privacy", annotation = "privacy")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Политика конфиденциальности")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp).padding(horizontal = 16.dp),
                style = TextStyle(color = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563)),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(offset, offset)
                        .firstOrNull()?.let { span ->
                            when (span.tag) {
                                "terms" -> onTermsClick()
                                "privacy" -> onPrivacyClick()
                            }
                        }
                }
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Уже есть аккаунт? Войти",
                    modifier = Modifier.clickable(onClick = onLoginClick),
                    color = if (darkTheme) MutedTextDark else Color.Gray
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text(
                        "ИЛИ",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
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

//@Composable
//fun InputField(
//    label: String,
//    value: String,
//    onValueChange: (String) -> Unit,
//    isError: Boolean = false
//) {
//    Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
//        Text(label, fontWeight = FontWeight.SemiBold)
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            isError = isError,
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp)
//        )
//    }
//}
