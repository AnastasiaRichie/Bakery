package com.bakery_tm.bakery.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.view_model.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingCartScreen(
    modifier: Modifier,
) {
    val viewModel = koinViewModel<FoodViewModel>()
    val state by viewModel.state.collectAsState()

    if (false) {
        EmptyShoppingCartUi(modifier)
    } else {
        ShoppingCartScreenUi(
            modifier,
        )
    }

}

@Composable
fun ShoppingCartScreenUi(
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "Shopping cart",
                style = MaterialTheme.typography.titleLarge
            )
        }
        LazyColumn {
            items(listOf<String>("Str")) {
                AddFoodItem()
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun AddFoodItem() {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            //modifier = Modifier,
            painter = painterResource(R.drawable.ic_ice_cream),
            contentDescription = null
        )
        Column(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
            Text(
                text = "Text Big",
                color = Color.LightGray,
                fontSize = 16.sp
            )
            Text(
                text = "Text Small",
                color = Color.DarkGray,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        CounterWithTextButtons(modifier = Modifier.padding(4.dp)) {
            // TODO (Обработать количество выбранного товара)
        }
        Image(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .padding(4.dp),
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
        Text("Nothing here", fontSize = 20.sp)
    }
}

@Composable
fun CounterWithTextButtons(
    modifier: Modifier = Modifier,
    initialValue: Int = 1,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE,
    onValueChange: (Int) -> Unit = {}
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
                    onValueChange(count)
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
                    onValueChange(count)
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