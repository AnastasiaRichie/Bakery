package com.bakery_tm.bakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.models.FoodModel
import com.bakery_tm.bakery.view_model.FoodViewModel

@Composable
fun FoodDetailsScreen(
    viewModel: FoodViewModel,
    modifier: Modifier,
    foodId: Long,
    onBackClicked: () -> Unit
) {
    val state by viewModel.selected.collectAsState()
    val orderCount by viewModel.orderCount.collectAsState()
    viewModel.initSelected(foodId)
    state?.let {
        FoodDetailsScreenUi(
            modifier,
            it,
            orderCount,
            onBackClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreenUi(
    modifier: Modifier,
    model: FoodModel,
    orderCount: Int,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val foodIconRes = remember(model.foodImageName) {
        context.resources.getIdentifier(model.foodImageName, "drawable", context.packageName)
    }

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = model.name,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            ),
            navigationIcon = {
                Button(
                    onBackClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.LightGray,
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .width(32.dp)
                            .height(40.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Назад"
                    )
                }
            },
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            if (foodIconRes != 0) {
                Image(
                    painter = painterResource(R.drawable.croissant),
                    modifier = Modifier
                        .width(128.dp)
                        .height(128.dp)
                        .align(Alignment.CenterHorizontally),
                    contentDescription = null
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Gray)
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text("No Image", color = Color.White)
                }
            }
            Text(
                model.weight,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(28.dp),
                color = Color.LightGray
            )
            Text(model.fullDescription, modifier = Modifier.padding(horizontal = 12.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            Button(onClick = {

            }) {
                Text("-")
            }
            Text(orderCount.toString())
            Button(onClick = {

            }) { Text("+")}
        }
    }
}