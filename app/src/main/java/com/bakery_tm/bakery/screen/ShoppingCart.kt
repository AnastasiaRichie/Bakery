package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSum by viewModel.cartSum.collectAsState()
    when {
        !isLoggedIn -> UnregisteredScreenUi(modifier)
        cartItems.isEmpty() -> EmptyShoppingCartUi(modifier)
        else -> ShoppingCartScreenUi(
            modifier = modifier,
            cartItems = cartItems,
            cartSum = cartSum,
            onDeleteClicked = { viewModel.deleteProduct(it) },
            onCreateOrder = { address -> orderViewModel.createOrder(address) },
        ) { add, productId -> viewModel.updateQuantity(add, productId) }
    }
}
val mockedAddresses = listOf(
    Address("1", "Витебск", "ул. Ленина, 12"),
    Address("2", "Минск", "пр. Победы, 45"),
    Address("3", "Минск", "ул. Горького, 3"),
    Address("4", "Гродно", "наб. Реки, 7"),
    Address("5", "Полоцк", "пер. Цветочный, 9")
)

@Composable
fun ShoppingCartScreenUi(
    modifier: Modifier,
    cartItems: List<CartItemWithProduct>,
    cartSum: Double,
    onDeleteClicked: (Long) -> Unit,
    onCreateOrder: (Address) -> Unit,
    onQuantityChanged: (Boolean, Long) -> Unit,
) {
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
                text = "Shopping cart",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 96.dp, bottom = 104.dp)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                AddFoodItem(item, onDeleteClicked, onQuantityChanged)
                HorizontalDivider()
            }
        }
        ChooseAddressForm(mockedAddresses, onCreateOrder)
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Итого:")
            Text("$cartSum BYN")
        }
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
fun ChooseAddressForm(
    addresses: List<Address>,
    onAddressSelected: (Address) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    var selected by remember { mutableStateOf<Address?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text("Выберите адрес заведения", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selected?.let { "${it.city} — ${it.address}" } ?: query,
                onValueChange = { query = it },
                readOnly = true,
                label = { Text("Адрес") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "menu")
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
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
                    filtered.forEach { addr ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(addr.city, fontWeight = FontWeight.Bold)
                                    Text(addr.address, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                selected = addr
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { selected?.let { onAddressSelected(it) } },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            enabled = selected != null
        ) {
            Text(
                text = "Оформить заказ",
                color = Color.Black,
                fontSize = 18.sp
            )
        }
    }
}

