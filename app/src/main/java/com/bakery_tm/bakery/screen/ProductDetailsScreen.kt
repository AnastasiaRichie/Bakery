package com.bakery_tm.bakery.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    modifier: Modifier,
    product: ProductDetails,
    onBack: () -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) BackgroundDark else BackgroundLight
    var count by remember { mutableIntStateOf(1) }

    Box(modifier.fillMaxSize().background(bg)) {

        LazyColumn {

            item {
                HeroImage(product.imageUrl, onBack)
            }

            item {
                Column(Modifier.padding(16.dp)) {
                    Text(product.name, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(product.price, fontSize = 24.sp, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text(product.description, color = Color.Gray)
                }
            }

            item { NutritionSection(product) }

            item { IngredientsSection(product.ingredients) }

            item { AllergensSection(product.allergens) }

            item { Spacer(Modifier.height(120.dp)) }
        }

        Row(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(4.dp)
        ) {
            IconButton(onClick = { if (count > 1) count-- }) { Icon(Icons.Default.Delete, null) }
            Text("$count", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { count++ }) { Icon(Icons.Default.Add, null) }
        }
    }
}

@Composable
fun HeroImage(imageUrl: String, onBack: () -> Unit) {
    Box(Modifier.height(400.dp)) {

//        AsyncImage(
//            model = imageUrl,
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )

//        Row(
//            Modifier.fillMaxWidth().padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            CircleIconButton(Icons.Default.ArrowBack, onBack)
//            Row {
//                CircleIconButton(Icons.Default.Favorite, {})
//                Spacer(Modifier.width(8.dp))
//                CircleIconButton(Icons.Default.Share, {})
//            }
//        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, BackgroundLight)
                    )
                )
        )
    }
}

@Composable
fun NutritionSection(p: ProductDetails) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Nutritional Value", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NutritionItem("Cals", p.calories)
            NutritionItem("Fat", p.fat)
            NutritionItem("Carbs", p.carbs)
            NutritionItem("Prot", p.protein)
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(
        modifier = Modifier
            //.weight(1f)
            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label.uppercase(), fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Primary)
    }
}

@Composable
fun IngredientsSection(text: String) {
    ExpandableSection(
        icon = Icons.Default.Menu,
        title = "Ingredients"
    ) {
        Text(text, fontSize = 13.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllergensSection(list: List<String>) {
    ExpandableSection(
        icon = Icons.Default.Warning,
        title = "Allergens"
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            list.forEach {
                AssistChip(
                    onClick = {},
                    label = { Text(it.uppercase()) }
                )
            }
        }
    }
}

@Composable
fun ExpandableSection(
    icon:  ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(Modifier.padding(horizontal = 16.dp)) {

        Row(
            Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Primary)
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(expanded) {
            Box(Modifier.padding(start = 36.dp, bottom = 12.dp)) {
                content()
            }
        }

        Divider()
    }
}

data class ProductDetails(
    val name: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val calories: String,
    val fat: String,
    val carbs: String,
    val protein: String,
    val ingredients: String,
    val allergens: List<String>
)