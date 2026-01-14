package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.models.FoodType
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import java.time.LocalTime

@Composable
fun FoodScreen(
    modifier: Modifier,
    isLoggedIn: Boolean,
    viewModel: FoodViewModel,
    userViewModel: UserViewModel,
    onFoodClick: (Long) -> Unit,
    onCartClicked: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()

    FoodScreenUi(
        modifier,
        state,
        isLoggedIn = isLoggedIn,
        user = userState.userStateModel?.name.orEmpty() + " " + userState.userStateModel?.surname,
        onFoodClick,
        onCartClicked,
        onRegisterClick
    )
}

@Composable
fun FoodScreenUi(
    modifier: Modifier,
    foodList: List<FoodModel>,
    isLoggedIn: Boolean,
    user: String,
    onFoodClick: (Long) -> Unit,
    onCartClicked: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    var selectedTab by remember { mutableIntStateOf(0) }
    Box(modifier
        .fillMaxSize()
        .background(background)) {
        Column {
            DashboardTopBar(isLoggedIn, user)
            if (!isLoggedIn) {
                ProductGuestBanner(onRegisterClick)
            }
            val tabs = listOf("Все", "Еда", "Напитки")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                color = if (isSelected) Primary.copy(alpha = 0.1f)
                                else Color.White
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isSelected) InputDark
                                else Color.Gray,
                                shape = RoundedCornerShape(32.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else BackgroundDark
                        )
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(min = 400.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = when (selectedTab) {
                        0 -> foodList
                        1 -> foodList.filter { it.foodType == FoodType.FLOUR }
                        2 -> foodList.filter { it.foodType == FoodType.DRINK }
                        else -> foodList
                    }) { ProductCard(product = it, onProductClick = onFoodClick, isLoggedIn = isLoggedIn) }
            }
        }
        CartFab(2, modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp), onCartClicked = onCartClicked)
    }
}

@Composable
fun FoodItem(food: FoodModel, onItemClick: (Long) -> Unit) {
    val context = LocalContext.current
    val foodIconRes = remember(food.foodImageName) {
        context.resources.getIdentifier(food.foodImageName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick(food.foodId) }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (foodIconRes != 0) {
            Image(
                painter = painterResource(foodIconRes),
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .padding(end = 16.dp, start = 8.dp),
                contentDescription = null
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.Gray)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", color = Color.White)
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = food.name,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = food.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
        Text(
            text = food.price,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp))
    }
}

@Composable
fun ProductCard(product: FoodModel, onProductClick: (Long) -> Unit, isLoggedIn: Boolean) {
    val context = LocalContext.current
    val foodIconRes = remember(product.foodImageName) {
        context.resources.getIdentifier(product.foodImageName, "drawable", context.packageName)
    }
    Column(modifier = Modifier.clickable { onProductClick(product.foodId) }) {
        Box(
            modifier = Modifier
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (foodIconRes != 0) {
                Image(
                    painter = painterResource(foodIconRes),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = Color.White)
                }
            }
            //TODO (обработать название)
            //Image(painter = painterResource(R.drawable.croissant), contentDescription = null)

//            IconButton(
//                onClick = {},
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(8.dp)
//            ) {
//                Icon(Icons.Default.FavoriteBorder, null, tint = Color.White)
//            }
        }
        Row(
            Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text(product.foodType.name, fontSize = 12.sp, color = Color.Gray)
                Text(product.price, color = Primary, fontWeight = FontWeight.Bold)
            }
            if (isLoggedIn) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(32.dp)
                        .background(Primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = BackgroundDark)
                }
            }
        }
    }
}

@Composable
fun CartFab(count: Int, modifier: Modifier, onCartClicked: () -> Unit) {
    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = onCartClicked,
            containerColor = Primary
        ) {
            Icon(Icons.Default.ShoppingCart, null, tint = BackgroundDark)
        }
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(18.dp)
                    .background(Color.White, CircleShape),
            ) {
                Text(
                    "$count",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center).offset(y = (-3).dp),
                    style = LocalTextStyle.current.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }
        }
    }
}

@Composable
fun ProductGuestBanner(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Присоединяйтесь к программе лояльности", color = Primary, fontWeight = FontWeight.Bold)
            Text("Зарабатывайте баллы за каждую покупку!", fontSize = 12.sp)
            Button(onClick = onRegisterClick, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text("Зарегистрироваться", color = BackgroundDark)
            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Найти кофе и снэки...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun DashboardTopBar(isLoggedIn: Boolean, user: String) {
    val greeting = remember { getGreeting() }
    Column {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Primary))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(greeting, fontSize = 12.sp, color = Color.Gray)
                    if (isLoggedIn) {
                        Text(user, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, null)
            }
        }
        SearchBar()
    }
}

fun getGreeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Доброе утро!"
        in 12..16 -> "Добрый день!"
        in 17..21 -> "Добрый вечер!"
        else -> "Доброй ночи!"
    }
}
