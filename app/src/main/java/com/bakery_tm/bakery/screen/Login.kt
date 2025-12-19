package com.bakery_tm.bakery.screen

import android.content.Intent
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.InputField
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
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
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
    )
}

@Composable
fun LoginScreenUi(
    modifier: Modifier,
    userStateModel: UserStateModel,
    error: String,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClicked: () -> Unit,
    onGuestClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        val emailFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.croissant),
            modifier = Modifier.fillMaxWidth(),
            contentDescription = null
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.imePadding()
        ) {
            InputField(
                name = userStateModel.email,
                label = stringResource(R.string.email),
                type = KeyboardType.Email,
                padding = 4,
                onValueChanged = onEmailChanged,
                currentRequest = emailFocusRequester,
                nextRequest = passwordFocusRequester
            )
            InputField(
                name = userStateModel.password,
                label = stringResource(R.string.password),
                type = KeyboardType.Password,
                padding = 4,
                visualTransformation = PasswordVisualTransformation(),
                onValueChanged = onPasswordChanged,
                currentRequest = passwordFocusRequester,
            )
            if (error.isNotEmpty()) {
                Text(error, fontSize = 12.sp, color = Color.Red)
            }
            Button(
                onClick = {
                    onLoginClick(userStateModel.email, userStateModel.password)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                enabled = Patterns.EMAIL_ADDRESS.matcher(userStateModel.email).matches() && userStateModel.password.length >= 4
            ) {
                Text("Login")
            }
            Button(
                onClick = onForgotClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            ) {
                Text("Forgot password")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Button(
                onClick = onSignUpClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            ) {
                Text("Sign up")
            }
            Button(
                onClick = onGuestClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            ) {
                Text("Continue as guest")
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
                style = TextStyle(color = Color.White),
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp).padding(horizontal = 4.dp),
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