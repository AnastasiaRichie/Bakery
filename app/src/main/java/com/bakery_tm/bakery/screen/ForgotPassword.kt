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
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val dark = isSystemInDarkTheme()
    val bg = if (dark) BackgroundDark else BackgroundLight
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
        onBack = onBackClicked,
    )
}

@Composable
fun ForgotPasswordScreenUi(
    modifier: Modifier,
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<ForgotPasswordViewModel>()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    LaunchedEffect(Unit) {
        viewModel.sendNotification.collect { pass ->
            showCopyNotification(context, pass)
            onBack()
        }
    }
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    val emailFocusRequester = remember { FocusRequester() }
    val state by viewModel.state.collectAsState()
    val userNotExistsEvent by viewModel.userNotExistsEvent.collectAsState(false)

    Box(modifier
        .fillMaxSize()
        .background(background)) {
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
                Text("Восстановить пароль", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(32.dp))
            }

            Text(
                text = "Забыли пароль?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )
            Text(
                "Если забыли пароль, введите почту, на которую был зарегистрирован аккаунт, и вам придет временный пароль. Войдите по нему и после в настройках аккаунта поменяйте его.",
                color = if (dark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            EmailField(
                value = state,
                onEmailChanged = { email -> viewModel.onEmailChanged(email) },
                emailFocusRequester = emailFocusRequester,
                onClear = { viewModel.onEmailChanged("") }
            )

            if (userNotExistsEvent) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        tint = Color.Red,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Мы не можем найти аккаунт, связанный с введенной почтой. Проверьте корректность введенных данных.",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Column(Modifier.padding(16.dp)) {
                Button(
                    onClick = { viewModel.isEmailExists() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Отправить Временный Пароль", color = BackgroundDark)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Send, null, tint = BackgroundDark)
                }

                TextButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                ) {
                    Text("Назад к Логину")
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(128.dp)
                .background(
                    Brush.linearGradient(listOf(Primary.copy(alpha = 0.3f), Color.Transparent)),
                    shape = RoundedCornerShape(topStart = 128.dp)
                )
        )
    }
}

@Composable
fun EmailField(
    value: String,
    emailFocusRequester: FocusRequester,
    onEmailChanged: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Почта", fontWeight = FontWeight.Medium)
        InputField(
            name = value,
            label = stringResource(R.string.email),
            type = KeyboardType.Email,
            padding = 4,
            onValueChanged = onEmailChanged,
            currentRequest = emailFocusRequester,
            placeholder = { Text("Например, example@mail.com") },
        )
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            placeholder = { Text("Например, example@mail.com") },
//            trailingIcon = {
//                if (value.isNotEmpty()) {
//                    IconButton(onClick = onClear) {
//                        Icon(Icons.Default.Clear, null)
//                    }
//                }
//            }
//        )
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