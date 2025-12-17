package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: FoodViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    modifier: Modifier,
    foodId: Long,
    onBackClicked: () -> Unit
) {
    val state by viewModel.selected.collectAsState()
    val cartItem by shoppingCartViewModel.cartItem.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed = lifecycleOwner.lifecycle.currentStateAsState().value == Lifecycle.State.RESUMED

    LaunchedEffect(foodId) {
        viewModel.initSelected(foodId)
    }
    LaunchedEffect(state?.id) {
        state?.let {
            shoppingCartViewModel.getCartInfoByProductId(it.id)
        }
    }
    state?.let {
        FoodDetailsScreenUi(
            modifier = modifier,
            isActive = isResumed,
            model = it,
            cartItem = cartItem,
            onQuantityChanged = { add ->
                shoppingCartViewModel.updateQuantity(add, it.id)
            },
            onAddClicked = { shoppingCartViewModel.addProduct(it.id) },
            onBackClicked = { onBackClicked() },
        )
    }
}

@Composable
fun FoodDetailsScreenUi(
    modifier: Modifier,
    isActive: Boolean,
    model: FoodModel,
    cartItem: CartItemEntity?,
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onQuantityChanged: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val foodIconRes = remember(model.foodImageName) {
        context.resources.getIdentifier(model.foodImageName, "drawable", context.packageName)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .padding(start = 4.dp, end = 16.dp)
                    .clickable { onBackClicked() },
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Назад"
            )
            Text(
                text = model.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 96.dp)
    ) {
        if (foodIconRes != 0) {
            Image(
                painter = painterResource(R.drawable.croissant),
                modifier = Modifier
                    .width(128.dp)
                    .height(128.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = null
            )
        } else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Gray)
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text("No Image", color = Color.White)
            }
        }
        Text(
            model.weight,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(28.dp),
            color = Color.LightGray
        )
        Text(model.fullDescription, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(modifier = Modifier.weight(1f))
        if (cartItem != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { onQuantityChanged(false) }, enabled = isActive) { Text("-") }
                Text(cartItem.quantity.toString())
                Button(onClick = { onQuantityChanged(true) }, enabled = isActive) { Text("+") }
            }
            Spacer(Modifier.height(52.dp))

        } else {
            Button(
                onClick = { onAddClicked() },
                enabled = isActive,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) { Text("Добавить в корзину") }
            Spacer(Modifier.height(52.dp))
        }
    }
}