package com.bakery_tm.bakery.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight
import com.bakery_tm.bakery.common.Primary
import com.bakery_tm.bakery.models.ProductModel
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun EditFoodScreen(
    viewModel: FoodViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
    darkTheme: Boolean,
    modifier: Modifier,
    productId: Long,
    onBackClicked: () -> Unit
) {
    val selected by viewModel.selected.collectAsState()
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    LaunchedEffect(productId) {
        shoppingCartViewModel.getCartInfoByProductId(productId)
        viewModel.initSelected(productId)
    }
    val isReady = selected != null
    BackHandler {
        shoppingCartViewModel.updateSelectedState()
        onBackClicked()
    }
    if (isReady) {
        selected?.let {
            EditFoodScreenUi(
                modifier = modifier,
                darkTheme = darkTheme,
                model = it,
                background = background,
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
fun EditFoodScreenUi(
    modifier: Modifier,
    darkTheme: Boolean,
    model: ProductModel,
    background: Color,
    onBackClicked: () -> Unit,
) {
    val context = LocalContext.current
    val foodIconRes = remember(model.productImageName) {
        context.resources.getIdentifier(model.productImageName, "drawable", context.packageName)
    }
    Box(modifier
        .fillMaxSize()
        .background(background)) {
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
                item { NutritionSection() }
                item { IngredientsSection(model.fullDescription) }
                item { AllergensSection(model.allergens, darkTheme) }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}