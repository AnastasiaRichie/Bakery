package com.bakery_tm.bakery.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.models.Address
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel

@Composable
fun ShoppingCartScreen(
    viewModel: ShoppingCartViewModel,
    orderViewModel: OrderViewModel,
    isLoggedIn: Boolean,
    modifier: Modifier,
    onToFoodListNavigate: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSum by viewModel.cartSum.collectAsState()
    val dark = isSystemInDarkTheme()
    val background = if (dark) BackgroundDark else BackgroundLight
    when {
//        !isLoggedIn -> UnregisteredScreenUi(modifier)
//        cartItems.isEmpty() -> EmptyShoppingCartUi(modifier)
        else -> ShoppingCartScreenUi(
            modifier = modifier,
            cartItems = cartItems,
            cartSum = cartSum,
            background = background,
            onLoginClick = onLoginClick,
            isLoggedIn = isLoggedIn,
            onToFoodListNavigate = onToFoodListNavigate,
            onDeleteClicked = { viewModel.deleteProduct(it) },
            onCreateOrder = { address -> orderViewModel.createOrder(address) },
        ) { add, productId -> viewModel.updateQuantity(add, productId) }
    }
}

val mockedAddresses = listOf(
    Address(1, "Витебск", "ул. Ленина, 12"),
    Address(2, "Минск", "пр. Победы, 45"),
    Address(3, "Минск", "ул. Горького, 3"),
    Address(4, "Гродно", "наб. Реки, 7"),
    Address(5, "Полоцк", "пер. Цветочный, 9")
)

@Composable
fun ShoppingCartScreenUi(
    modifier: Modifier,
    cartItems: List<CartItemWithProduct>,
    cartSum: Double,
    background: Color,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onToFoodListNavigate: () -> Unit,
    onDeleteClicked: (Long) -> Unit,
    onCreateOrder: (Address) -> Unit,
    onQuantityChanged: (Boolean, Long) -> Unit,
) {
    var address by remember { mutableStateOf(mockedAddresses[0]) }
    Box(modifier
        .fillMaxSize()
        .background(background)) {
        Column {
            CartTopBar()
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 108.dp)
            ) {
                if (!isLoggedIn) { item { GuestBanner(onLoginClick) } }
                item { AddressSelector(mockedAddresses, address) { address = it } }
                item { SectionHeader(cartItems.sumOf { it.item.quantity }) }
                if (cartItems.isEmpty()) {
                    item { EmptyState(onToFoodListNavigate) }
                } else {
                    //TODO (divider)
                    items(cartItems) { item -> CartItemRow(item, onQuantityChanged, onDeleteClicked) }
                }
            }
        }
        CheckoutPanel(
            total = cartSum,
            enabled = cartItems.isNotEmpty(),
            modifier = Modifier.align(Alignment.BottomCenter),
            onCreateOrder = { onCreateOrder(address) }
        )
    }
}

@Composable
fun AddFoodItem(
    item: CartItemWithProduct,
    onDeleteClicked: (Long) -> Unit,
    onQuantityChanged: (Boolean, Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(R.drawable.ic_ice_cream),
            contentDescription = null
        )
        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
            .weight(9f)) {
            Text(
                text = item.product.name,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
            Text(
                text = item.product.description,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        CounterWithTextButtons(
            modifier = Modifier.padding(4.dp),
            initialValue = item.item.quantity,
            onValueChanged = { onQuantityChanged(it, item.product.productId) })
        Image(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .padding(4.dp)
                .clickable { onDeleteClicked(item.item.cartItemId) },
            colorFilter = ColorFilter.tint(Color.White),
            painter = painterResource(R.drawable.ic_trash),
            contentDescription = "Delete"
        )
    }
}

@Composable
fun EmptyShoppingCartUi(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.ic_add_bag_basket),
            modifier = Modifier
                .width(192.dp)
                .height(192.dp),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(36.dp))
        Text("Корзина пуста", fontSize = 20.sp)
    }
}

@Composable
fun CounterWithTextButtons(
    modifier: Modifier = Modifier,
    initialValue: Int,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE,
    onValueChanged: (Boolean) -> Unit
) {
    var count by remember { mutableIntStateOf(initialValue) }
    Row(
        modifier = modifier
            .height(32.dp)
            .width(106.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (count > minValue) {
                    count--
                    onValueChanged(false)
                }
            },
            enabled = count > minValue
        ) {
            Text(
                text = "-",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (count > minValue) Color.Black else Color.Gray
            )
        }
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        IconButton(
            onClick = {
                if (count < maxValue) {
                    count++
                    onValueChanged(true)
                }
            },
            enabled = count < maxValue
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (count < maxValue) Color.Black else Color.Gray
            )
        }
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
                Text("Синхронизируйте корзину для сбора бонусов и получения заказа", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSelector(
    addresses: List<Address>,
    selectedAddress: Address,
    onAddressSelected: (Address) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Card(
            Modifier
                .padding(horizontal = 16.dp)
                .menuAnchor()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, null, tint = Primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Адрес получения заказа:", fontSize = 10.sp, color = Color.Gray)
                    Text("${selectedAddress.city} — ${selectedAddress.address}", fontWeight = FontWeight.Medium)
                }
            }
        }
        val filtered = if (query.isBlank()) addresses else addresses.filter {
            (it.city + " " + it.address).contains(query, ignoreCase = true)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (filtered.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Ничего не найдено") },
                    onClick = { /* ничего */ }
                )
            } else {
                filtered.forEach { address ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(address.city, fontWeight = FontWeight.Bold)
                                Text(address.address, style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        onClick = {
                            onAddressSelected(address)
                            expanded = false
                        }
                    )
                }
            }
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
        Text(elementsLabel(count), color = Primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CartItemRow(
    item: CartItemWithProduct,
    onQuantityChanged: (Boolean, Long) -> Unit,
    onDeleteClicked: (Long) -> Unit,
) {
    val context = LocalContext.current
    val foodIconRes = remember(item.product.productImageName) {
        context.resources.getIdentifier(item.product.productImageName, "drawable", context.packageName)
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp).padding(start = 10.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.padding(horizontal = 8.dp).weight(1f)) {
                        Text(
                            text = item.product.name,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(item.product.description, fontSize = 13.sp, color = Color.Gray)
                    }
                    Text(item.product.price, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CounterWithTextButtons(
                    modifier = Modifier.padding(4.dp),
                    initialValue = item.item.quantity,
                    onValueChanged = { onQuantityChanged(it, item.product.productId) }
                )
                IconButton(onClick = { onDeleteClicked(item.item.cartItemId) }) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun EmptyState(onToFoodListNavigate: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ShoppingCart, null, tint = Primary, modifier = Modifier.size(96.dp))
        Spacer(Modifier.height(12.dp))
        Text("Ваша корзина пустая", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Похоже, что вы пока ничего не добавили.", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onToFoodListNavigate, colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = BackgroundDark)) {
            Text("К покупкам!")
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
    //TODO(скругленные углы и рамка)
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        SummaryRow("Итого", total, highlight = true)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCreateOrder,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = BackgroundDark)
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
            "${"%.2f".format(value)} BYN",
            color = if (highlight) Primary else Color.Gray,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun elementsLabel(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100

    val word = when {
        mod10 == 1 && mod100 != 11 -> "элемент"
        mod10 in 2..4 && mod100 !in 12..14 -> "элемента"
        else -> "элементов"
    }

    return "$count $word"
}