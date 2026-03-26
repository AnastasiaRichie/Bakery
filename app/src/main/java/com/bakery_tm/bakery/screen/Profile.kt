package com.bakery_tm.bakery.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight
import com.bakery_tm.bakery.common.InputDark
import com.bakery_tm.bakery.common.Primary
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import android.graphics.Color as AndroidColor

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    orderViewModel: OrderViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    darkTheme: Boolean,
    modifier: Modifier,
    onLogOutClicked: () -> Unit,
    onLogInClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val avatar by viewModel.selectedAvatar.collectAsState()
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NavigationEvent.NavigateToRegister -> onLogOutClicked()
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
            ProfileScreenUi(
                modifier,
                model,
                avatar,
                darkTheme,
                background,
                onEditClicked
            ) {
                orderViewModel.onLogoutClicked()
                shoppingCartViewModel.onLogoutClicked()
                viewModel.onLogOutClicked()
            }
        }
        model == null -> {
            GuestProfileScreen(
                modifier,
                darkTheme,
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
    avatar: ProfileAvatar,
    darkTheme: Boolean,
    background: Color,
    onEditClicked: () -> Unit,
    onLogOutClicked: () -> Unit
) {
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        item { ProfileHeader(model, avatar, onEditClicked) }
        item { QrCard(model.email, darkTheme) }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary.copy(alpha = 0.6f),
                    contentColor = Color.White
                ),
                onClick = onLogOutClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            ) { Text("Выйти") }
        }
    }
}

@Composable
fun GuestProfileScreen(
    modifier: Modifier,
    darkTheme: Boolean,
    onLogInClicked: () -> Unit,
    onRegisterClicked: () -> Unit
) {
    val background = if (darkTheme) BackgroundDark else BackgroundLight

    Box(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))
                GuestIllustration()
                Spacer(Modifier.height(24.dp))
                GuestMessage()
                Spacer(Modifier.height(40.dp))
                GuestButtons(onLogInClicked, onRegisterClicked)
            }
        }
    }
}

@Composable
fun GuestIllustration() {
    Box(modifier = Modifier
        .size(220.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Primary.copy(alpha = 0.1f), CircleShape)
                .blur(48.dp)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.White, CircleShape)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = BackgroundDark,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}

@Composable
fun GuestMessage() {
    Column(horizontalAlignment = CenterHorizontally) {
        Text(
            "Вы просматриваете страницу в качестве гостя",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Войдите или зарегистрируйтесь, чтобы делать заказы, получать их и копить баллы.",
            fontSize = 14.sp,
            color = Primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GuestButtons(onLogInClicked: () -> Unit, onRegisterClicked: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onLogInClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Войти", fontWeight = FontWeight.Bold, color = BackgroundLight)
        }
        OutlinedButton(
            onClick = onRegisterClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            border = BorderStroke(2.dp, Primary.copy(alpha = 0.2f))
        ) {
            Text("Зарегистрироваться", color = Primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QrWithCloseDialog(
    bitmap: ImageBitmap,
    darkTheme: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.wrapContentSize()) {
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
                    tint = if (darkTheme) Color.White else BackgroundDark
                )
            }
        }
    }
}

private fun generateQrBitmap(content: String, size: Int = 600): Bitmap {
    val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val bmp = createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp[x, y] = if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
        }
    }
    return bmp
}

@Composable
fun QrCard(email: String, darkTheme: Boolean) {
    var showQr by remember { mutableStateOf(false) }

    val qrContent = """
        {"id":"Bakery", "token":"$email"}
    """.trimIndent()

    val bitmap = remember(email) {
        generateQrBitmap(qrContent)
    }
    if (showQr) {
        QrWithCloseDialog(bitmap.asImageBitmap(), darkTheme) { showQr = false }
    }
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (darkTheme) InputDark else Primary.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp, top = 16.dp)) {
            Text("Ваш QR-код", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                "Отсканируйте на кассе для получения заказа.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 16.dp)
                .height(128.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(CenterHorizontally)
                    .clickable { showQr = true },
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Коснитесь, чтобы открыть QR",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .clickable { showQr = true })
        }
    }
}

@Composable
fun ProfileHeader(user: UserStateModel, avatar: ProfileAvatar, onEditClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onEditClicked) { Icon(Icons.Default.Edit, null) }
        }
        Box(
            modifier = Modifier
                .size(140.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(avatar.iconRes),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(user.name + " " + user.lastName, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(user.email, color = Primary)
    }
}
