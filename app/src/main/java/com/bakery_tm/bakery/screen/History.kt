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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.dateFormatter
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.view_model.OrderViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier,
    viewModel: OrderViewModel,
    isLoggedIn: Boolean,
    onLoginClicked: () -> Unit,
    onOrderClicked: (Long, Int) -> Unit
) {
    val orders by viewModel.orders.collectAsState()

    when {
        !isLoggedIn -> UnregisteredScreenUi(modifier, onLoginClicked)
        orders.isEmpty() -> EmptyHistoryScreenUi(modifier)
        else -> HistoryScreenUi(modifier, orders, onOrderClicked)
    }
}

@Composable
fun HistoryScreenUi(
    modifier: Modifier,
    orders: List<OrderWithItems>,
    onOrderClicked: (Long, Int) -> Unit
) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    Box(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderHistoryTopBar()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                itemsIndexed(orders) { index, order ->
                    OrderCard(order = order, index = index)
                    HistoryItem(order, onOrderClicked, index)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(order: OrderWithItems, onOrderClicked: (Long, Int) -> Unit, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE5E5E5), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
            .clickable { onOrderClicked(order.order.orderId, index) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Заказ №${index + 1}",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Text(
                text = dateFormatter(order.order.date).orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            text = "City: ${order.order.orderAddress.city}, street: ${order.order.orderAddress.address}",
            fontSize = 13.sp,
            color = Color(0xFF444444),
            modifier = Modifier.padding(top = 3.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Заказ: ${order.items.map { it.product.name }}",
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Состав заказа: ${order.items.map { it.product.name }}",
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EmptyHistoryScreenUi(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 96.dp)
            .background(Color.DarkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 112.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_search_bucket),
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text("Вы не заказывали ничего \nпоследние 90 дней.", fontSize = 20.sp)
        }
    }
}

@Composable
fun OrderHistoryTopBar() {
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Text("История заказов", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun OrderCard(order: OrderWithItems, index: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                "Заказ: ${index + 1}",
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                dateFormatter(order.order.date).orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )


//            Text(
//                order.order.orderAddress.toString(),
//                color = Color(0xFF22C55E),
//                fontSize = 11.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .background(Color(0xFF22C55E).copy(alpha = 0.15f), CircleShape)
//                    .padding(horizontal = 12.dp, vertical = 4.dp)
//            )
        }
        val localOrder = order.items.map {
            it.orderItem.quantity.toString() + "x" + it.product.name
        }
        Spacer(Modifier.height(8.dp))
        Text("Заказ: " + localOrder.toString(), fontSize = 13.sp, color = Color.Gray)
        Text("Место выдачи: " + localOrder.toString(), fontSize = 13.sp, color = Color.Gray)

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

            //TODO(TOTAL)
            Text(order.order.userOwnerId.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            PrimaryButton("Заказать заново", {})
        }
    }
}