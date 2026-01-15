package com.bakery_tm.bakery.screen

import android.content.Intent
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun LoginScreen(
    modifier: Modifier,
    viewModel: RegistrationViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    orderViewModel: OrderViewModel,
    onFoodNavigation: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClicked: () -> Unit,
) {
    val darkTheme = isSystemInDarkTheme()
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateToFood -> {
                    shoppingCartViewModel.getShoppingCart()
                    orderViewModel.getOrders()
                    onFoodNavigation()
                }
                NavigationEvent.NavigateToRegister -> onSignUpClick()
                is NavigationEvent.ShowError -> {
                    error = event.message
                    println("Ошибка: ${event.message}")
                }
                else -> Unit
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.authState.collect { authState ->
            if (authState == AuthState.Authenticated) {
                onFoodNavigation()
            }
        }
    }

    LoginScreenUi(
        modifier = modifier,
        userStateModel = state,
        error = error,
        background = background,
        darkTheme = darkTheme,
        showPassword = showPassword,
        onLoginClick = viewModel::onLoginClick,
        onSignUpClick = viewModel::onSignUpClick,
        onForgotClicked = onForgotClicked,
        onGuestClick = viewModel::onGuestClick,
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
        onEmailChanged = { value -> viewModel.onLoginValueChanged(FieldType.EMAIL, value) },
        onPasswordChanged = { value -> viewModel.onLoginValueChanged(FieldType.PASSWORD, value) },
        onShowPasswordChanged = { show -> showPassword = show },
    )
}

@Composable
fun LoginScreenUi(
    modifier: Modifier,
    userStateModel: UserStateModel,
    error: String,
    background: Color,
    darkTheme: Boolean,
    showPassword: Boolean,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClicked: () -> Unit,
    onGuestClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onShowPasswordChanged: (Boolean) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
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
                    .background(background)
            ) {
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
                if (error.isNotEmpty()) { ErrorBox(error) }
                Spacer(Modifier.height(8.dp))
                InputField(
                    label = "Email",
                    value = userStateModel.email,
                    onValueChange = { onEmailChanged(it) }
                )
                PasswordField(
                    value = userStateModel.password,
                    onValueChange = { onPasswordChanged(it) },
                    show = showPassword,
                    onToggle = { onShowPasswordChanged(!showPassword) }
                )
                TextButton(
                    onClick = onForgotClicked,
                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
                ) { Text("Забыли пароль?", color = Primary) }
                PrimaryButton("Войти") {
                    onLoginClick(userStateModel.email, userStateModel.password)
                }
                OutlinedButton(
                    onClick = onGuestClick,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp)
                ) { Text("Продолжить как Гость") }
                Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp)) {
                    Text("Нет аккаунта?")
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Зарегистрироваться",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSignUpClick).padding(bottom = 8.dp)
                    )
                }
                val annotatedString = buildAnnotatedString {
                    append("By clicking continue, you agree to our ")
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Terms of Service")
                    }
                    pop()
                    append(" and ")
                    pushStringAnnotation(tag = "privacy", annotation = "privacy")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Privacy Policy")
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedString,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp).padding(horizontal = 16.dp),
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
            }
        }
    }
}

@Composable
fun ErrorBox(text: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
                    Icon(if (show) painterResource(R.drawable.visibility_on) else painterResource(R.drawable.visibility_off), null)
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

val Primary = Color(0xFF2BEE6C)
val BackgroundLight = Color(0xFFF6F8F6)
val BackgroundDark = Color(0xFF102216)
val InputDark = Color(0xFF193322)
val BorderDark = Color(0xFF326744)
val MutedTextDark = Color(0xFF92C9A4)