package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.common.InputField
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.UserViewModel

@Composable
fun EditScreen(
    viewModel: UserViewModel,
    modifier: Modifier,
    onBackClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val avatar by viewModel.selectedAvatar.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> onBackClicked()
                else -> Unit
            }
        }
    }
    state.userStateModel?.let { data ->
        EditProfileScreen(
            modifier,
            data,
            avatar,
            { selectedAvatar -> viewModel.selectAvatar(selectedAvatar) },
            onBackClicked,
            { viewModel.onEditDataSaved(it, it.userId) },
        )
    }
}

@Composable
fun EditScreenUi(
    modifier: Modifier,
    fieldEnteredValue: String,
    onSaveClicked: () -> Unit,
    onValueChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
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
                text = "Редактировать",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.imePadding()
        ) {
            InputField(
                name = fieldEnteredValue,
                label = "",
                padding = 4,
                onValueChanged = onValueChanged,
                currentRequest = focusRequester,
            )
            Button(
                onClick = onSaveClicked,
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
                enabled = fieldEnteredValue.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun EditProfileScreen(
    modifier: Modifier,
    data: UserStateModel,
    avatar: ProfileAvatar,
    onAvatarSelected: (ProfileAvatar) -> Unit,
    onBack: () -> Unit,
    onSave: (UserStateModel) -> Unit,
) {
    var firstName by remember { mutableStateOf(data.name) }
    var lastName by remember { mutableStateOf(data.surname.orEmpty()) }
    var email by remember { mutableStateOf(data.email) }
    var password by remember { mutableStateOf("") }
    var model by remember { mutableStateOf(data) }

    Column(
        modifier = modifier.fillMaxSize().background(InputDark)
    ) {
        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text("Редактировать профиль", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(32.dp))
        }
        Text("Иконка профиля", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            ProfileAvatar.entries.forEach { localAvatar ->
                val isSelected = localAvatar == avatar
                Image(
                    painter = painterResource(localAvatar.iconRes),
                    contentDescription = localAvatar.name,
                    modifier = Modifier
                        .size(54.dp)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) Primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onAvatarSelected(localAvatar) }
                )
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Персональные данные")
            Spacer(Modifier.height(8.dp))
            FloatingField(firstName, { firstName = it }, "Имя")
            FloatingField(lastName, { lastName = it }, "Фамилия (опционально)")
            Text("Конфиденциальные данные")
            Spacer(Modifier.height(8.dp))
            FloatingField(email, { email = it }, "Почта")
            FloatingField(password, { password = it }, "Пароль", isPassword = true)
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onSave(
                        model.copy(
                            name = firstName,
                            surname = lastName,
                            email = email,
                            password = password,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Сохранить", color = InputDark, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Отменить", color = Color.White.copy(0.6f))
            }
        }
    }
}

@Composable
fun FloatingField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = BorderDark,
            focusedContainerColor = InputDark,
            unfocusedContainerColor = InputDark,
            focusedLabelColor = BackgroundLight,
            unfocusedLabelColor = Color.White.copy(0.6f),
            cursorColor = BackgroundLight,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        supportingText = {
            if (isPassword) {
                Text("Для сохранения пароля оставьте поле пустым", color = Color.Gray, fontSize = 10.sp)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}