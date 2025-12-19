package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bakery_tm.bakery.common.mapper.toDomain
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.OrderViewModel

@Composable
fun HistoryDetailsScreen(
    viewModel: OrderViewModel,
    modifier: Modifier,
    orderId: Long,
    index: Int,
) {
    val order by viewModel.order.collectAsState()
    viewModel.getDetailedOrder(orderId)
    if (order == null) {
        LoadingScreen()
    } else {
        order?.let {
            HistoryDetailsUi(
                modifier,
                it,
                index,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailsUi(
    modifier: Modifier,
    order: OrderWithItems,
    index: Int,
) {
    val orderSum = order.items.sumOf { it.product.price.replace(" BYN", "").toDouble() * it.orderItem.quantity }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "Заказ № ${index + 1}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 96.dp, bottom = 48.dp)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(order.items) { item ->
                OrderItem(item.product.toDomain(), item.orderItem.quantity)
                HorizontalDivider()
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Сумма заказа:")
            Text("$orderSum BYN")
        }
    }
}

@Composable
fun OrderItem(food: FoodModel, quantity: Int) {
    val context = LocalContext.current
    val foodIconRes = remember(food.foodImageName) {
        context.resources.getIdentifier(food.foodImageName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = food.name,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = food.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            Text(
                text = food.price,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "(x$quantity)",
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}
