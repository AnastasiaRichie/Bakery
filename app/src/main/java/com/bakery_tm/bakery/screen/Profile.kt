package com.bakery_tm.bakery.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.UserViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import android.graphics.Color as AndroidColor

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    modifier: Modifier,
    onLogOutClicked: () -> Unit,
    onLogInClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onEditClicked: (AccountFieldType) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateToRegister -> onLogOutClicked()
                is NavigationEvent.NavigateToEdit -> onEditClicked(event.type)
                is NavigationEvent.ShowError -> println("Ошибка: ${event.message}")
                else -> Unit
            }
        }
    }
    val model = state.userStateModel
    when {
        state.isLoading -> {
            LoadingScreen()
        }
        model != null -> {
            ProfileScreenUi(modifier, model,viewModel::onEditClicked) {
                viewModel.onLogOutClicked(model.email)
            }
        }
        model == null -> {
            UnregisteredProfileScreenUi(
                modifier,
                onLogInClicked,
                onRegisterClicked
            )
        }
    }
}

@Composable
fun ProfileScreenUi(
    modifier: Modifier,
    model: UserStateModel,
    onEditClicked: (AccountFieldType) -> Unit,
    onLogOutClicked: () -> Unit
) {
    var showQr by remember { mutableStateOf(false) }

    val qrContent = """
        {"id":"Bakery", "token":"${model.email}"}
    """.trimIndent()

    val bitmap = remember(model) {
        generateQrBitmap(qrContent)
    }
    if (showQr) {
        QrWithCloseDialog( bitmap.asImageBitmap()) {
            showQr = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 96.dp, bottom = 104.dp)
            .padding(horizontal = 16.dp)
    ) {
        AccountRow(label = "Name", value = model.name, type = AccountFieldType.NAME, onEditClicked = onEditClicked)
        AccountRow(label = "Surname", value = model.surname, type = AccountFieldType.SURNAME, onEditClicked = onEditClicked)
        AccountRow(label = "Email", value = model.email, type = AccountFieldType.EMAIL, onEditClicked = onEditClicked)
        AccountRow(label = "Password", value = model.password, type = AccountFieldType.PASSWORD, onEditClicked = onEditClicked)

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Ваш QR-код",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(CenterHorizontally)
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(64.dp)
                    .align(CenterHorizontally)
                    .clickable { showQr = true },
                contentScale = ContentScale.Fit
            )
            Text("Коснитесь, чтобы открыть QR", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier
                .align(CenterHorizontally)
                .clickable { showQr = true })
        }

        Text(
            "Отсканируйте на кассе для получения заказа",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogOutClicked,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Logout",
                color = Color.Black,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun UnregisteredProfileScreenUi(
    modifier: Modifier,
    onLogInClicked: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onLogInClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = Color.Gray,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(top = 8.dp),
        ) {
            Text("Login")
        }
        Button(
            onClick = onRegisterClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.Black,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(top = 8.dp),
        ) {
            Text("Sign up")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            fontSize = 12.sp,
            text = "Вы не вошли в аккаунт и не можете делать заказы. Войдите или зарегистрируйтесь, чтобы иметь доступ ко всем возможностям приложения",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AccountRow(
    label: String,
    value: String? = null,
    type: AccountFieldType,
    underline: Boolean = false,
    onEditClicked: (AccountFieldType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value.orEmpty(),
                color = Color.Gray,
                fontSize = 16.sp,
                textDecoration = if (underline) TextDecoration.Underline else null,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_edit),
            contentDescription = "Edit",
            tint = Color.Gray,
            modifier = Modifier
                .size(28.dp)
                .padding(horizontal = 4.dp)
                .clickable {
                    onEditClicked(type)
                }
        )
    }
    HorizontalDivider(color = Color(0xFFEAEAEA))
}

@Composable
fun QrWithCloseDialog(
    bitmap: ImageBitmap,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

private fun generateQrBitmap(content: String, size: Int = 600): Bitmap {
    val bitMatrix = MultiFormatWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size
    )

    val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp[x, y] = if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
        }
    }
    return bmp
}

enum class AccountFieldType {
    NAME,
    SURNAME,
    EMAIL,
    PASSWORD,
}