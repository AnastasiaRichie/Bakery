package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier,
    viewModel: ShoppingCartViewModel,
    onOrderClicked: (Long) -> Unit
) {
    val orders by viewModel.orders.collectAsState()

    if (orders.isEmpty()) {
        EmptyHistoryScreenUi(modifier)
    } else {
        HistoryScreenUi(
            modifier,
            orders,
            onOrderClicked
        )
    }
}

@Composable
fun HistoryScreenUi(
    modifier: Modifier,
    orders: List<OrderWithItems>,
    onOrderClicked: (Long) -> Unit
) {
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

        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 112.dp)
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) {
                HistoryItem(it, onOrderClicked)
            }
        }
    }
}

@Composable
fun HistoryItem(order: OrderWithItems, onOrderClicked: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE5E5E5), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
            .clickable { onOrderClicked(order.order.orderId) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dateFormatter(order.order.date).orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Sum:",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Text(
            text = "City: ${order.order.orderAddress.city}, street: ${order.order.orderAddress.address}",
            fontSize = 13.sp,
            color = Color(0xFF444444),
            modifier = Modifier.padding(top = 3.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Заказ: ${order.items.map { it.product.name }}",
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "?",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
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
