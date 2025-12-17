package com.bakery_tm.bakery.screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bakery_tm.bakery.common.InputField
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.view_model.UserViewModel

@Composable
fun EditScreen(
    viewModel: UserViewModel,
    modifier: Modifier,
    type: AccountFieldType,
    onSaveClicked: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> onSaveClicked()
                else -> Unit
            }
        }
    }
    var fieldEnteredValue by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        state.userStateModel?.let {
            fieldEnteredValue = when(type) {
                AccountFieldType.NAME -> it.name
                AccountFieldType.SURNAME -> it.surname.orEmpty()
                AccountFieldType.EMAIL -> it.email
                AccountFieldType.PASSWORD -> ""
            }
        }
    }
    state.userStateModel?.let {
        EditScreenUi(modifier, fieldEnteredValue, {
            viewModel.onEditDataSaved(type, fieldEnteredValue, it.userId)
        }) { value -> fieldEnteredValue = value }
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