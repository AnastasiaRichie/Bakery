package com.bakery_tm.bakery.screen

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onStartOrderingClicked: () -> Unit,
    onOrderClicked: (Long, Int) -> Unit
) {
    val orders by viewModel.orders.collectAsState()

    when {
        !isLoggedIn -> UnregisteredScreenUi(modifier, onLoginClicked)
        orders.isEmpty() -> EmptyHistoryScreenUi(modifier, onStartOrderingClicked)
        else -> HistoryScreenUi(modifier, orders, onOrderClicked) { orderId ->
            viewModel.reorder(orderId)
        }
    }
}

@Composable
fun HistoryScreenUi(
    modifier: Modifier,
    orders: List<Pair<OrderWithItems, Double>>,
    onOrderClicked: (Long, Int) -> Unit,
    reorder: (Long) -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    Box(modifier = modifier.fillMaxSize().background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderHistoryTopBar()
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(orders) { index, order ->
                    OrderCard(order = order, index = index, reorder = reorder, onOrderClicked = onOrderClicked)
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryScreenUi(modifier: Modifier, onStartOrderingClicked: () -> Unit) {
    Box(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        Column(Modifier.fillMaxSize()) {
            OrderHistoryTopBar()
            Spacer(Modifier.weight(1f))
            EmptyOrderState(onStartOrderingClicked)
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun EmptyOrderState(onStartOrderingClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(192.dp)
                .background(Primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.receipt_long),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Primary.copy(alpha = 0.4f)
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.BottomEnd)
                    .offset((-12).dp, (-12).dp)
                    .background(BackgroundDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Primary
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Заказов пока нет",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "История ваших заказов появится здесь после того, как вы сделаете свой первый заказ.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onStartOrderingClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = BackgroundDark),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { Text("Перейти к ассортименту") }
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
fun OrderCard(order: Pair<OrderWithItems, Double>, index: Int, reorder: (Long) -> Unit, onOrderClicked: (Long, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputDark, RoundedCornerShape(16.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onOrderClicked(order.first.order.orderId, index) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    "Заказ #${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    dateFormatter(order.first.order.date,  "MMM dd, yyyy • HH:mm")?.replaceFirstChar { it.uppercase() }.orEmpty(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            StatusBadge("Получен")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = order.first.items.joinToString { "${it.orderItem.quantity} x ${it.product.name}" },
            fontSize = 13.sp,
            color = Color.LightGray,
            fontStyle = FontStyle.Italic
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "ИТОГО",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    "${order.second} BYN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { reorder(order.first.order.orderId) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = BackgroundDark),
            ) { Text("Повторить заказ") }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .background(BackgroundDark, CircleShape)
            .border(1.dp, Primary, CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            status.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}