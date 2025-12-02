package com.bakery_tm.bakery.screen

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.InputField
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(modifier: Modifier) {
    val viewModel = koinViewModel<RegistrationViewModel>()

    ForgotPasswordScreenUi(
        modifier = modifier,
        email = "",
        onForgotClicked = {},
        onEmailChanged = { value -> viewModel.onLoginValueChanged(FieldType.EMAIL, value) },
    )
    // TODO("Заменить моки на свойства")
}

@Composable
fun ForgotPasswordScreenUi(
    modifier: Modifier,
    email: String,
    onEmailChanged: (String) -> Unit,
    onForgotClicked: () -> Unit,
) {
    val emailFocusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.imePadding()
        ) {
            Text(
                fontSize = 12.sp,
                text = "Если забыли пароль, введите почту, на которую был зарегистрирован аккаунт, и вам придет временный пароль. Войдите по нему и после в настройках аккаунта поменяйте его.",
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            )
            InputField(
                name = email,
                label = stringResource(R.string.email),
                type = KeyboardType.Email,
                padding = 4,
                onValueChanged = onEmailChanged,
                currentRequest = emailFocusRequester,
            )
            Button(
                onClick = onForgotClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp),
                enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            ) {
                Text("Forgot")
                // TODO("Отправлять рандомный пароль в шторку, при клике на уведомление копировать его в буфер обмена")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
