package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight
import com.bakery_tm.bakery.common.BorderDark
import com.bakery_tm.bakery.common.Glass
import com.bakery_tm.bakery.common.Primary
import com.bakery_tm.bakery.domain.OrderResponse
import com.bakery_tm.bakery.domain.OrderResponseItem
import com.bakery_tm.bakery.domain.OrderState
import com.bakery_tm.bakery.models.Address
import com.bakery_tm.bakery.view_model.OrderViewModel

@Composable
fun HistoryDetailsScreen(
    viewModel: OrderViewModel,
    modifier: Modifier,
    darkTheme: Boolean,
    orderId: Long,
    index: Int,
    onBackClicked: () -> Unit
) {
    val order by viewModel.order.collectAsState()
    var showDialog by remember { mutableStateOf("") }
    viewModel.getDetailedOrder(orderId)
    LaunchedEffect(Unit) {
        viewModel.onBack.collect {
            onBackClicked()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.showUnavailableProduct.collect { showDialog = it }
    }
    if (showDialog.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showDialog = "" },
            title = {
                Text("Ошибка заказа")
            },
            text = { Text(showDialog) },
            confirmButton = {
                TextButton(onClick = { showDialog = "" }) {
                    Text("OK")
                }
            }
        )
    }

    if (order == null) {
        LoadingScreen()
    } else {
        order?.let {
            HistoryDetailsUi(
                modifier,
                it,
                darkTheme,
                index,
                { viewModel.reorder(it.orderId) },
                onBackClicked,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailsUi(
    modifier: Modifier,
    order: OrderResponse,
    darkTheme: Boolean,
    index: Int,
    onReorderClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    Column(modifier = modifier
        .fillMaxSize()
        .background(background)) {
        OrderDetailsTopBar(index, onBackClicked, order.orderState, darkTheme)
        LazyColumn(modifier = Modifier
            .padding(horizontal = 16.dp)
            .weight(1f)) {
            item {
                Column {
                    Spacer(Modifier.height(12.dp))
                    OrderItems(order.items, darkTheme)
                    Spacer(Modifier.height(12.dp))
                    PaymentSummary(order, darkTheme)
                    Spacer(Modifier.height(12.dp))
                    PickupLocation(order.address, darkTheme)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
        ReorderButton(onClick = onReorderClicked)
    }
}

@Composable
fun OrderDetailsTopBar(index: Int, onBackClicked: () -> Unit, orderState: OrderState, darkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (darkTheme) BackgroundDark.copy(alpha = 0.9f) else BackgroundLight)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = if (darkTheme) Color.White else Color.Black)
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "ДЕТАЛИ ЗАКАЗА",
                fontSize = 12.sp,
                color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Text(
                "#${index + 1}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (darkTheme) Color.White else Color.Black
            )
        }
        Text(
            if (orderState == OrderState.ORDERED) "Заказан" else "Получен",
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .background(Primary.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            color = Primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OrderItemRow(title: String, subtitle: String, price: String, foodImageName: String, darkTheme: Boolean) {
    val context = LocalContext.current
    val foodIconRes = remember(foodImageName) {
        context.resources.getIdentifier(foodImageName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
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
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (darkTheme) Color.White else Color.Black)
            Text(subtitle, fontSize = 12.sp, color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f))
        }
        Text(price, fontWeight = FontWeight.Bold, color = if (darkTheme) Color.White else Color.Black)
    }
}

@Composable
fun PaymentSummary(order: OrderResponse, darkTheme: Boolean) {
    val orderSum = order.items.sumOf { it.product.price.replace(" BYN", "").replace(",", ".").toDouble() * it.quantity }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(darkTheme, "Сумма оплаты", painterResource(R.drawable.receipt_long))
        GlassCard(darkTheme) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Итоговая стоимость", fontSize = 12.sp, color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                Text("${"%.2f".format(orderSum)} BYN", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            }
        }
    }
}

@Composable
fun PickupLocation(orderAddress: Address, darkTheme: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
            Text(
                "Адрес получения",
                color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        GlassCard(darkTheme) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocationOn, null, tint = Primary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(orderAddress.city, fontWeight = FontWeight.Bold, color = if (darkTheme) Color.White else Color.Black)
                    Text(orderAddress.address, fontSize = 12.sp, color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@Composable
fun GlassCard(darkTheme: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (darkTheme) Glass else BackgroundLight, RoundedCornerShape(16.dp))
            .border(1.dp, if (darkTheme) Color.White.copy(alpha = 0.05f) else BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun ReorderButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Primary.copy(alpha = 0.4f))))
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = BackgroundLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("ПОВТОРИТЬ ЗАКАЗ", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SectionTitle(
    darkTheme: Boolean,
    title: String,
    icon: Painter
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            title,
            color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionDate() {
    Column {
        Text(
            "TRANSACTION DATE",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
        Text(
            "October 24, 2023",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

@Composable
fun OrderItems(orderItems: List<OrderResponseItem>, darkTheme: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            "Заказ",
            color = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(Modifier.height(12.dp))
    GlassCard(darkTheme) {
        orderItems.forEachIndexed { index, item ->
            OrderItemRow(
                item.product.name,
                "Кол-во: ${item.quantity}",
                item.product.price,
                item.product.productImageName,
                darkTheme
            )
            if (index != orderItems.size - 1) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    }
}