package com.bakery_tm.bakery.screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.CopyReceiver
import com.bakery_tm.bakery.common.InputField
import com.bakery_tm.bakery.view_model.ForgotPasswordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    onBackClicked: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    val context = LocalContext.current
    val viewModel = koinViewModel<ForgotPasswordViewModel>()
    val state by viewModel.state.collectAsState()
    val userNotExistsEvent by viewModel.userNotExistsEvent.collectAsState(false)
    LaunchedEffect(Unit) {
        viewModel.sendNotification.collect { pass ->
            showCopyNotification(context, pass)
            onBackClicked()
        }
    }
    ForgotPasswordScreenUi(
        modifier = modifier,
        email = state,
        userNotExistsEvent = userNotExistsEvent,
        onForgotClicked = { viewModel.isEmailExists() },
        onEmailChanged = { email -> viewModel.onEmailChanged(email) },
    )
}

@Composable
fun ForgotPasswordScreenUi(
    modifier: Modifier,
    email: String,
    userNotExistsEvent: Boolean,
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
                text = "Забыли пароль?",
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
            if (userNotExistsEvent) {
                Text("Пользователя с таким email нет!", fontSize = 12.sp, color = Color.Red)
            }
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
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

fun showCopyNotification(context: Context, value: String) {
    val channelId = "copy_channel"

    val intent = Intent(context, CopyReceiver::class.java).apply {
        putExtra("COPY_TEXT", value)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val channel = NotificationChannel(channelId, "Copy channel", NotificationManager.IMPORTANCE_DEFAULT)
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle("Нажмите, чтобы скопировать временный пароль")
        .setContentText(value)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    NotificationManagerCompat.from(context).notify(1, notification)
}