package com.bakery_tm.bakery.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryDetailsScreen(
    modifier: Modifier,
    onFoodClick: (Int) -> Unit
) {
    val viewModel = koinViewModel<FoodViewModel>()
    val state by viewModel.state.collectAsState()

    HistoryDetailsUi(
        modifier,
        state,
        onFoodClick
    )
}

@Composable
fun HistoryDetailsUi(
    modifier: Modifier,
    foodList: List<FoodModel>,
    onFoodClick: (Int) -> Unit
) {
    // TODO ("Добавить просмотр деталей заказа")
}