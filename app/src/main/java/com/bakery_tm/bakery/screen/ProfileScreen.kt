package com.bakery_tm.bakery.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier,
    user: UserProfile,
    onEdit: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOrderHistory: () -> Unit = {},
    onPayments: () -> Unit = {}
) {
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Profile") },
//                navigationIcon = {
//                    IconButton(onClick = {}) {
//                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
//                    }
//                },
//                actions = {
//                    TextButton(onClick = onEdit) {
//                        Text("Edit", color = MaterialTheme.colorScheme.primary)
//                    }
//                }
//            )
//        }
//    ) { padding ->
        LazyColumn(
            modifier = modifier
                //.padding(padding)
                .fillMaxSize()
        ) {
            item { ProfileHeader(user) }
            item { QrCard(user.qrUrl) }
            item {
                Spacer(Modifier.height(16.dp))
                //ProfileActionItem(Icons.Default.Refresh, "Order History", onOrderHistory)
                //ProfileActionItem(Icons.Default.ShoppingCart, "Payment Methods", onPayments)
            }
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Text("Logout")
                }
            }
        }
}

@Composable
fun ProfileHeader(user: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
//            AsyncImage(
//                model = user.avatarUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
        }

        Spacer(Modifier.height(12.dp))

        Text(user.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(user.email, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    }
}

@Composable
fun QrCard(qrUrl: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF193322))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            AsyncImage(
//                model = qrUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(180.dp)
//                    .background(Color.White, RoundedCornerShape(12.dp))
//                    .padding(16.dp),
//                contentScale = ContentScale.Fit
//            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    "Pickup Code",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp
                )
                Text("Scan to Pickup Order", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "Show this QR code at the counter to collect your fresh snacks and coffee instantly.",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProfileActionItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF193322))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(text, Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(0.4f))
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val avatarUrl: String,
    val qrUrl: String
)