package com.bakery_tm.bakery.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bakery_tm.bakery.view_model.OrdersViewModel

@Composable
fun ManagerFoodScannerScreen(
    modifier: Modifier,
    viewModel: OrdersViewModel
) {
    var scannedEmail by remember { mutableStateOf<String?>(null) }
    var isScannerOpen by remember { mutableStateOf(false) }
    val orders by viewModel.orders.collectAsState(emptyList())
    val allOrders by viewModel.allOrders.collectAsState(emptyList())
    LaunchedEffect(Unit) {
        viewModel.getAllOrders()
    }
    when {
        isScannerOpen -> {
            CameraScreen { email ->
                scannedEmail = email
                isScannerOpen = false
                viewModel.fetchOrdersByEmail(email)
            }
        }
        scannedEmail != null -> {
            OrdersList(
                modifier = modifier,
                orders = orders,
                onBackClicked = { scannedEmail = null },
                onOrderReceived = { orderId -> viewModel.markOrderReceived(orderId) }
            )
        }
        else -> {
            OrdersList(
                modifier = modifier,
                orders = allOrders,
                showScanButton = true,
                onScanClicked = { isScannerOpen = true },
                onOrderReceived = { orderId -> viewModel.markOrderReceived(orderId) }
            )
        }
    }
}
