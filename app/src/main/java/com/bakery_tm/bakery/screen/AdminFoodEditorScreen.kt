package com.bakery_tm.bakery.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.common.BackgroundDark
import com.bakery_tm.bakery.common.BackgroundLight
import com.bakery_tm.bakery.common.InputDark
import com.bakery_tm.bakery.common.Primary
import com.bakery_tm.bakery.common.Red
import com.bakery_tm.bakery.models.ProductModel
import com.bakery_tm.bakery.view_model.FoodViewModel

@Composable
fun AdminFoodEditorScreen(
    modifier: Modifier,
    viewModel: FoodViewModel,
    darkTheme: Boolean,
    onEditProductNavigate: (Long) -> Unit,
) {
    val productList by viewModel.state.collectAsState()
    val unavailableProducts by viewModel.unavailableProducts.collectAsState()
    val background = if (darkTheme) BackgroundDark else BackgroundLight
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize().background(background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AdminFoodTopBar()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(productList) { product ->
                    FoodEditorItem(
                        product,
                        context,
                        viewModel::removeProduct,
                        onEditProductNavigate
                    )
                }
                item {
                    Text("Удаленные товары")
                }
                items(unavailableProducts) { product ->
                    FoodEditorItem(
                        product,
                        context,
                        viewModel::returnBackProduct,
                        onEditProductNavigate,
                        isReturn = true,
                    )
                }
            }
        }
    }
}

@Composable
fun FoodEditorItem(
    product: ProductModel,
    context: Context,
    onButtonClick: (Long) -> Unit,
    onEditProductNavigate: (Long) -> Unit,
    isReturn: Boolean = false,
) {
    val foodIconRes = remember(product.productImageName) {
        context.resources.getIdentifier(product.productImageName, "drawable", context.packageName)
    }
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (foodIconRes != 0) {
            Image(
                painter = painterResource(foodIconRes),
                modifier = Modifier.size(64.dp).padding(8.dp),
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
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Bold)
            Text(product.description, fontSize = 12.sp, color = Color.Gray)
            Text(product.price, color = Primary, fontWeight = FontWeight.Bold)
        }
        IconButton(
            onClick = { onButtonClick(product.productId) },
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterVertically)
                .padding(4.dp)
        ) {
            if (isReturn) {
                Icon(Icons.Default.Refresh, null, tint = InputDark)
            } else {
                Icon(Icons.Default.Delete, null, tint = Red)
            }
        }
        IconButton(
            onClick = { onEditProductNavigate(product.productId) },
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterVertically)
                .padding(4.dp)
        ) { Icon(Icons.Default.KeyboardArrowRight, null, tint = BackgroundDark) }
    }
}

@Composable
fun AdminFoodTopBar() {
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Text("Редактирование данных", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
    }
}