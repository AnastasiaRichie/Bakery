package com.bakery_tm.bakery.screen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: FoodViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    isLoggedIn: Boolean,
    modifier: Modifier,
    foodId: Long,
    onBackClicked: () -> Unit
) {
    val selected by viewModel.selected.collectAsState()
    val state by shoppingCartViewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed =
        lifecycleOwner.lifecycle.currentStateAsState().value == Lifecycle.State.RESUMED || lifecycleOwner.lifecycle.currentStateAsState().value == Lifecycle.State.STARTED
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    LaunchedEffect(foodId) {
        shoppingCartViewModel.getCartInfoByProductId(foodId)
        viewModel.initSelected(foodId)
    }
    val isReady = selected != null && !state.isLoading
    BackHandler {
        shoppingCartViewModel.updateSelectedState()
        onBackClicked()
    }
    if (isReady) {
        selected?.let {
            FoodDetailsScreenUi(
                modifier = modifier,
                isActive = isResumed,
                isLoggedIn = isLoggedIn,
                model = it,
                background = background,
                cartItem = state.cartItem,
                onQuantityChanged = { add ->
                    shoppingCartViewModel.updateQuantity(add, it.foodId)
                },
                onAddClicked = {
                    shoppingCartViewModel.addProduct(it.foodId)
                },
                onBackClicked = {
                    shoppingCartViewModel.updateSelectedState()
                    onBackClicked()
                }
            )
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun FoodDetailsScreenUi(
    modifier: Modifier,
    isActive: Boolean,
    isLoggedIn: Boolean,
    model: FoodModel,
    background: Color,
    cartItem: CartItemEntity?,
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onQuantityChanged: (Boolean) -> Unit,
) {
    var count by remember { mutableIntStateOf(cartItem?.quantity ?: 0) }
    val context = LocalContext.current
    val foodIconRes = remember(model.foodImageName) {
        context.resources.getIdentifier(model.foodImageName, "drawable", context.packageName)
    }
    Box(modifier.fillMaxSize().background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item { HeroImage(foodIconRes, onBackClicked) }
                item {
                    Column(Modifier.padding(16.dp)) {
                        Text(model.name, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text(
                            model.price,
                            fontSize = 24.sp,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(model.description, color = Color.Gray)
                    }
                }
                item { NutritionSection(model) }
                item { IngredientsSection(model.fullDescription) }
                item { AllergensSection(model.allergens) }
                item { Spacer(Modifier.height(120.dp)) }
            }



            if (count != 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = BackgroundDark
                        ),
                        onClick = {
                            count--
                            onQuantityChanged(false)
                        }, enabled = isActive
                    ) { Text("-") }
                    Text(count.toString())
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = BackgroundDark
                        ),
                        onClick = {
                            count++
                            onQuantityChanged(true)
                        }, enabled = isActive
                    ) { Text("+") }
                }
            } else {
                Button(
                    onClick = {
                        count++
                        onAddClicked()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = BackgroundDark
                    ),
                    enabled = isActive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { Text("Добавить в корзину") }
            }
        }


    }
}

@Composable
fun HeroImage(@DrawableRes icon: Int, onBack: () -> Unit) {
    Box(Modifier.height(400.dp)) {
        if (icon != 0) {
            Image(
                painter = painterResource(icon),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", color = Color.White)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIconButton(icon = Icons.Default.ArrowBack, onClick = onBack)
            Row {
                CircleIconButton(icon = Icons.Default.Favorite, onClick = {})
                Spacer(Modifier.width(8.dp))
                CircleIconButton(icon = Icons.Default.Share, onClick = {})
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, BackgroundLight)))
        )
    }
}

@Composable
fun NutritionSection(model: FoodModel) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Nutritional Value", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            NutritionItem("Cals", model.calories)
//            NutritionItem("Fat", model.fat)
//            NutritionItem("Carbs", model.carbs)
//            NutritionItem("Prot", model.protein)
            NutritionItem("Cals", "12")
            NutritionItem("Fat", "34")
            NutritionItem("Carbs", "56")
            NutritionItem("Prot", "78")
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(
        modifier = Modifier
            //.weight(1f)
            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label.uppercase(), fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Primary)
    }
}

@Composable
fun IngredientsSection(text: String) {
    ExpandableSection(
        icon = Icons.Default.Menu,
        title = "Ingredients"
    ) {
        Text(text, fontSize = 13.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
//fun AllergensSection(list: List<String>) {
fun AllergensSection(list: String) {
    Text(list)
    ExpandableSection(
        icon = Icons.Default.Warning,
        iconColor = Color.Yellow,
        title = "Аллергены"
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            list.forEach {
                AssistChip(
                    onClick = {},
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color.Yellow.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color.Yellow.copy(alpha = 0.1f)),
                    label = { Text(it.uppercase(), color = Color.Yellow, fontSize = 12.sp) }
                )
            }
        }
    }
}

@Composable
fun ExpandableSection(
    icon: ImageVector,
    iconColor: Color = Primary,
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = iconColor)
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(expanded) {
            Box(Modifier.padding(start = 36.dp, bottom = 12.dp)) {
                content()
            }
        }

        Divider()
    }
}

@Composable
fun CircleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Primary.copy(alpha = 0.1f), CircleShape)
            .border(1.dp, Primary.copy(alpha = 0.2f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}
