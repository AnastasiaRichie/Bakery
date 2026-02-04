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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    viewModel: RegistrationViewModel,
    onBack: () -> Unit,
    onLoginClick: () -> Unit,
    onSuccessClick: () -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var error by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateToFood, NavigationEvent.NavigateToRegister -> onSuccessClick()
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
        dark = dark,
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
        onBack = onBack,
    )
}

@Composable
fun RegistrationScreenUi(
    modifier: Modifier,
    userStateModel: UserStateModel,
    error: String,
    background: Color,
    dark: Boolean,
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
    onBack: () -> Unit,
) {
    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
//            Row(
//                modifier = Modifier.padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, null)
//                }
//                Spacer(Modifier.weight(1f))
//                Text("Регистрация", style = MaterialTheme.typography.titleLarge)
//                Spacer(Modifier.weight(1f))
//                Spacer(Modifier.width(32.dp))
//            }
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

            InputField("Имя", userStateModel.name) { onNameChanged(it) }
            InputField("Фамилия (опционально)", userStateModel.lastName.orEmpty()) { onSurnameChanged(it) }

            InputField(label = "Почта", value = userStateModel.email, onValueChange = { onEmailChanged(it) }, isError = error.isNotEmpty())

            if (error.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(start = 20.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    //Text(text = "Эта почта уже зарегистрирована. ", color = Color.Red, fontSize = 12.sp)
                    Text(text = "$error. ", color = Color.Red, fontSize = 12.sp)
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
                value = userStateModel.password,
                onValueChange = { onPasswordChanged(it) },
                show = showPassword,
                onToggle = { onShowChanged(!showPassword) }
            )

            Button(
                onClick = { onRegisterClick(userStateModel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Зарегистрироваться", color = BackgroundDark, fontSize = 18.sp)
            }

            val annotatedString = buildAnnotatedString {
                append("By clicking continue, you agree to our ")

                pushStringAnnotation(
                    tag = "terms",
                    annotation = "terms"
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Terms of Service")
                }
                pop()

                append(" and ")

                pushStringAnnotation(
                    tag = "privacy",
                    annotation = "privacy"
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Privacy Policy")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp).padding(horizontal = 16.dp),
                style = TextStyle(color = if (dark) Color(0xFF9CA3AF) else Color(0xFF4B5563)),
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

//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.Center,
//        ) {
//            Text(
//                text = "Registration",
//                style = MaterialTheme.typography.titleLarge
//            )
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        Column(
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.imePadding()
//        ) {
//            InputField(
//                userStateModel.name,
//                stringResource(R.string.name),
//                onValueChanged = onNameChanged,
//                currentRequest = nameFocusRequester,
//                nextRequest = surnameFocusRequester,
//            )
//            InputField(
//                userStateModel.surname.orEmpty(),
//                stringResource(R.string.surname_optional),
//                onValueChanged = onSurnameChanged,
//                currentRequest = surnameFocusRequester,
//                nextRequest = emailFocusRequester,
//            )
//            InputField(
//                userStateModel.email,
//                stringResource(R.string.email),
//                KeyboardType.Email,
//                onValueChanged = onEmailChanged,
//                currentRequest = emailFocusRequester,
//                nextRequest = passwordFocusRequester,
//            )
//            InputField(
//                userStateModel.password,
//                stringResource(R.string.password),
//                KeyboardType.Password,
//                PasswordVisualTransformation(),
//                onValueChanged = onPasswordChanged,
//                currentRequest = passwordFocusRequester,
//            )
//            if (error.isNotEmpty()) {
//                Text(error, fontSize = 12.sp, color = Color.Red)
//            }
//            Spacer(modifier = Modifier.height(18.dp))
//            Button(
//                onClick = { onRegisterClick(userStateModel) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .padding(top = 8.dp),
//                enabled = userStateModel.name.isNotBlank()
//                        && Patterns.EMAIL_ADDRESS.matcher(userStateModel.email).matches()
//                        && userStateModel.password.length >= 4
//            ) {
//                Text("Register")
//            }
//        }
        //Spacer(modifier = Modifier.weight(1f))

    }
//}

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
