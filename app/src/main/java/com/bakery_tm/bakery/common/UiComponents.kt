package com.bakery_tm.bakery.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun InputField(
    name: String,
    label: String,
    type: KeyboardType? = null,
    visualTransformation: VisualTransformation? = null,
    onValueChanged: (String) -> Unit,
    currentRequest: FocusRequester,
    padding: Int = 0,
    nextRequest: FocusRequester? = null,
    placeholder: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = name,
        onValueChange = onValueChanged,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().focusRequester(currentRequest).padding(padding.dp),
        singleLine = true,
        placeholder = placeholder,
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = type ?: KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { nextRequest?.requestFocus() ?: focusManager.clearFocus() })
    )
}