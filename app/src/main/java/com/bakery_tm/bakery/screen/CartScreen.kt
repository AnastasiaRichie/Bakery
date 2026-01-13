package com.bakery_tm.bakery.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    modifier: Modifier,
    cartItems: List<CartItem>,
    onLoginClick: () -> Unit
) {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) BackgroundDark else BackgroundLight
    val total = cartItems.sumOf { it.price * it.qty }

    Box(modifier.fillMaxSize().background(bg)) {
        Column {
            CartTopBar()
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 108.dp)
            ) {
                item { GuestBanner(onLoginClick) }
                item { AddressSelector() }
                item { SectionHeader(cartItems.size) }
                if (cartItems.isEmpty()) {
                    item { EmptyState() }
                } else {
                    items(cartItems) { item -> CartItemRow(item) }
                }
            }
        }
        CheckoutPanel(
            total = total,
            enabled = cartItems.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter),
            onCreateOrder = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar() {
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Text("Моя корзина", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun GuestBanner(onLoginClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row {
                    Text("Войдите, чтобы начать копить", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Button(onClick = onLoginClick, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                        Text("Войти")
                    }
                }
                Text("Синхронизируйте корзину для получения бонусов и оплаты за них", fontSize = 13.sp, color = Color.Gray)
            }

        }
    }
}

@Composable
fun AddressSelector() {
    Card(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, null, tint = Primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Забрать:", fontSize = 10.sp, color = Color.Gray)
                Text("221B Baker Street, London", fontWeight = FontWeight.Medium)
            }
            TextButton(onClick = {}) { Text("Изменить") }
        }
    }
}

@Composable
fun SectionHeader(count: Int) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Заказ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("$count элементов", color = Primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CartItemRow(item: CartItem) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

//        AsyncImage(
//            model = item.imageUrl,
//            contentDescription = null,
//            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
//            contentScale = ContentScale.Crop
//        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text("$${"%.2f".format(item.price)}", fontWeight = FontWeight.Bold)
            }

            Text(item.subtitle, fontSize = 13.sp, color = Color.Gray)

            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                QuantitySelector(item)

                IconButton(onClick = {}) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun QuantitySelector(item: CartItem) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { if (item.qty > 1) item.qty-- }) {
            Icon(Icons.Default.Delete, null)
        }
        Text("${item.qty}", fontWeight = FontWeight.Bold)
        IconButton(onClick = { item.qty++ }) {
            Icon(Icons.Default.Add, null)
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ShoppingCart, null, tint = Primary, modifier = Modifier.size(96.dp))
        Spacer(Modifier.height(12.dp))
        Text("Ваша корзина пустая", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Похоже, что вы пока ничего не добавили.", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text("Start Shopping")
        }
    }
}

@Composable
fun CheckoutPanel(
    total: Double,
    enabled: Boolean,
    modifier: Modifier,
    onCreateOrder: () -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        SummaryRow("Total Price", total, highlight = true)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCreateOrder,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Заказать", fontSize = 18.sp)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: Double, highlight: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (highlight) Color.Unspecified else Color.Gray)
        Text(
            "$${"%.2f".format(value)}",
            color = if (highlight) Primary else Color.Gray,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}



data class CartItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val price: Double,
    val imageUrl: String,
    var qty: Int
)