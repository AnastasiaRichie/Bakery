package com.bakery_tm.bakery.screen

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.FoodViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.view_model.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier,
    onLogOutClicked: () -> Unit,
    onLogInClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    val isRegistered by viewModel.isRegistered.collectAsState()

    if (isRegistered) {
        ProfileScreenUi(
            modifier,
            state,
            onLogOutClicked,
        )
    } else {
        UnregisteredProfileScreenUi(
            modifier,
            onLogInClicked,
            onRegisterClicked
        )
    }

}

@Composable
fun ProfileScreenUi(
    modifier: Modifier,
    model: UserStateModel,
    onFoodClick: () -> Unit
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
        AccountRow(label = "Name", value = model.name)
        AccountRow(label = "Surname", value = model.surname)
        AccountRow(label = "Email", value = model.email)
        AccountRow(label = "Password", value = model.password)

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Ваш QR-код",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(CenterHorizontally)
        )

        Image(
            painter = painterResource(android.R.drawable.ic_menu_camera),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(140.dp)
                .padding(8.dp)
                .align(CenterHorizontally),
            contentScale = ContentScale.Fit
        )
        Text(
            "Отсканируйте на кассе для получения заказа",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onFoodClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
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
    underline: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = CenterVertically,
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
            modifier = Modifier.size(20.dp)
        )
    }
    HorizontalDivider(color = Color(0xFFEAEAEA))
}