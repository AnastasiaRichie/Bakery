package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.models.FoodType
import com.bakery_tm.bakery.view_model.FoodViewModel

@Composable
fun FoodScreen(
    viewModel: FoodViewModel,
    onFoodClick: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    FoodScreenUi(
        state,
        onFoodClick
    )
}

@Composable
fun FoodScreenUi(
    foodList: List<FoodModel>,
    onFoodClick: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 56.dp)
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 27.0.dp),
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "Food & drinks",
                style = MaterialTheme.typography.titleLarge
            )
        }
        val tabs = listOf("All", "Flours", "Drinks")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            color = if (isSelected) Color.Black
                            else Color.White
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isSelected) Color.Black
                            else Color.Gray,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color.DarkGray
                    )
                }
            }
        }
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(
                items = when (selectedTab) {
                    0 -> foodList
                    1 -> foodList.filter { it.foodType == FoodType.FLOUR }
                    2 -> foodList.filter { it.foodType == FoodType.DRINK }
                    else -> foodList
                }) { FoodItem(it, onFoodClick) }
        }
    }
}

@Composable
fun FoodItem(food: FoodModel, onItemClick: (Long) -> Unit) {
    val context = LocalContext.current
    val foodIconRes = remember(food.foodImageName) {
        context.resources.getIdentifier(food.foodImageName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick(food.id) }
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
            Text(
                text = food.name,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = food.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
        Text(
            text = food.price,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp))
    }
}
