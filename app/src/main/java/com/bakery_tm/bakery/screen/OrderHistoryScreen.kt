package com.bakery_tm.bakery.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderHistoryScreen(
    modifier: Modifier
) {
    val orders = remember {
        listOf(
            Order(
                id = "5829",
                date = "Oct 24, 10:30 AM",
                status = OrderStatus.Completed,
                items = "1x Iced Latte, 1x Butter Croissant",
                total = "$12.50",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBy7XVR7HNZwm8gs5l_vxIvZH3lL7ED0AKvDrxzPZ25y2v2uhYgBtg-MfgXF8-IcdIpC7Xz8J9zzlU72hfnSPvEhN23Z1XsxBLcHeO0DDOL0fPSixwOPhrXQDtF6mmfW2ACE9ZzVMU5HAHFmvVrJinTu89EM97v6E4pauj67vKOrDGrEgNoxpuanBudG2CbX2T1uowKgpsOxY0qUK30v7xusVyt0NlvnpQpggXW4uSfjzQeDB_Bjp520O777jwJvf62Tca3npHEEao1"
            ),
            Order(
                id = "5711",
                date = "Oct 20, 09:15 AM",
                status = OrderStatus.Completed,
                items = "2x Cappuccino, 1x Blueberry Muffin",
                total = "$18.20",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCXbnoseURNdVajIhsohMD4dv0wNXYGTL40BvQSfdxucxq_MwKLbSRO91FJaPpdNYHqRLD3SiK0XgSBSosUl8uwb7fgmZNQ5pUwdaFtwrviotKGyfHJ_Zszx9N0KXPM7hQe_FR80MsiatSet4p7Yt5_8MflC3dvLO_JNSyD_nkfVwkU_K-UU6O9fQNzLaW-W0HC68FqAHk-pal3otD8jA50IMTmMhg3vvNC7kSLkF-VQGSvNPgLRqDOomlGHWkeLUqteX3U1uHeVNYO"
            ),
            Order(
                id = "5492",
                date = "Oct 10, 02:20 PM",
                status = OrderStatus.Cancelled,
                items = "1x Cold Brew",
                total = "$5.50",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBeOzTP41-QR-UGCqdRK-SnJ5Wz0Ul_IZGgeCgvELgxuzXU86nMiejSdlQRaZodRXJBc4_D4ZzfBMDTnjvGhAwl1Iu3PqjyL8TQR_gW1TpblEWFmFJnQ8auwRp_QW_gk2kaXjugfuNxd6kXQJay_6mle7mJED8CZJe8BR5UiZHoJejh9b8KQlcfc82_rw52biR3GBd0drDrFYR4bxWt_ai3rEUJ7fvKqPAokLmh3RyUMlK3ZplTyEaC2GPGGKBofMfJsR6ZG_0zH1BL"
            )
        )
    }


    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text(
                "Recent Orders",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(orders) { order ->
            OrderCard(order)
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun OrderCard(order: Order) {
    val cardAlpha = if (order.status == OrderStatus.Cancelled) 0.6f else 1f

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
//            AsyncImage(
//                model = order.imageUrl,
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(120.dp)
//            )

            Column(Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        order.status.name,
                        color = if (order.status == OrderStatus.Completed)
                            MaterialTheme.colorScheme.primary else Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(order.date, style = MaterialTheme.typography.labelSmall)
                }

                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(order.items, style = MaterialTheme.typography.bodySmall)
                        Text("Total: ${order.total}", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* TODO */ },
                        enabled = order.status == OrderStatus.Completed
                    ) {
                        Text(if (order.status == OrderStatus.Completed) "Reorder" else "Details")
                    }
                }
            }
        }
    }
}


data class Order(
    val id: String,
    val date: String,
    val status: OrderStatus,
    val items: String,
    val total: String,
    val imageUrl: String
)

enum class OrderStatus {
    Completed, Cancelled
}