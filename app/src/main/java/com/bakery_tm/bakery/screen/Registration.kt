package com.bakery_tm.bakery.screen

import android.content.Intent
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
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
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    viewModel: RegistrationViewModel,
    onSuccessClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var error by remember { mutableStateOf("") }

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
    )
}

@Composable
fun RegistrationScreenUi(
    modifier: Modifier,
    userStateModel: UserStateModel,
    error: String,
    onRegisterClick: (UserStateModel) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSurnameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {
    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Registration",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.imePadding()
        ) {
            InputField(
                userStateModel.name,
                stringResource(R.string.name),
                onValueChanged = onNameChanged,
                currentRequest = nameFocusRequester,
                nextRequest = surnameFocusRequester,
            )
            InputField(
                userStateModel.surname.orEmpty(),
                stringResource(R.string.surname_optional),
                onValueChanged = onSurnameChanged,
                currentRequest = surnameFocusRequester,
                nextRequest = emailFocusRequester,
            )
            InputField(
                userStateModel.email,
                stringResource(R.string.email),
                KeyboardType.Email,
                onValueChanged = onEmailChanged,
                currentRequest = emailFocusRequester,
                nextRequest = passwordFocusRequester,
            )
            InputField(
                userStateModel.password,
                stringResource(R.string.password),
                KeyboardType.Password,
                PasswordVisualTransformation(),
                onValueChanged = onPasswordChanged,
                currentRequest = passwordFocusRequester,
            )
            if (error.isNotEmpty()) {
                Text(error, fontSize = 12.sp, color = Color.Red)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { onRegisterClick(userStateModel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                enabled = userStateModel.name.isNotBlank()
                        && Patterns.EMAIL_ADDRESS.matcher(userStateModel.email).matches()
                        && userStateModel.password.length >= 4
            ) {
                Text("Registrate")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
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
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
            style = TextStyle(color = Color.White),
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
