package com.bakery_tm.bakery.screen

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bakery_tm.bakery.domain.OrderResponse
import com.bakery_tm.bakery.domain.OrderState
import com.bakery_tm.bakery.view_model.OrdersViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Date

@Composable
fun QrScannerScreen(modifier: Modifier, viewModel: OrdersViewModel) {
    var scannedEmail by remember { mutableStateOf<String?>(null) }
    if (scannedEmail == null) {
        CameraScreen({
            scannedEmail = it
            viewModel.fetchOrdersByEmail(it)
        }) {}
    } else {
        val orders by viewModel.orders.collectAsState(emptyList())
        OrdersList(
            modifier = modifier,
            orders = orders,
            onBackClicked = { scannedEmail = null },
            onOrderReceived = { orderId -> viewModel.markOrderReceived(orderId) }
        )
    }
}


@Composable
fun OrdersList(
    modifier: Modifier,
    orders: List<OrderResponse>,
    onBackClicked: () -> Unit,
    onOrderReceived: (Long) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
            items(orders, key = { it.orderId }) { order ->
                OrderCard(order = order, onMarkReceived = { onOrderReceived(it.orderId) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Button(onBackClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Назад")
        }
    }
}

@Composable
fun OrderCard(order: OrderResponse, onMarkReceived: (OrderResponse) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Заказ #${order.orderId}", style = MaterialTheme.typography.titleMedium)
            Text("Адрес: ${order.address.city}, ${order.address.address}")
            Text("Дата: ${Date(order.date).toLocaleString()}")

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                order.items.forEach { item -> Text("${item.quantity} x ${item.product.name}") }
            }

            if (order.orderState == OrderState.ORDERED) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onMarkReceived(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.Black)
                ) {
                    Text("Забрал заказ")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(onEmailScanned: (String) -> Unit, onCameraReady: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(key1 = cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            onCameraReady()
        }
    }
    when {
        cameraPermissionState.status.isGranted -> {
            QrScannerScreenUi {
                try {
                    val payload = Json.decodeFromString<QrPayload>(it)
                    onEmailScanned(payload.token)
                } catch (e: Exception) {
                    Log.e("Exception", "Ошибка при парсинге QR: ${e.message}")
                }
            }
        }

        cameraPermissionState.status.shouldShowRationale -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Для сканирования QR кода нужна камера")
                Spacer(modifier = Modifier.height(8.dp))
                Button({ cameraPermissionState.launchPermissionRequest() })  {
                    Text("Запросить разрешение")
                }
            }
        }

        else -> {
            // Первый запуск или пользователь отклонил
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Разрешение камеры не предоставлено. Для работы приложения разрешите доступ")
                Spacer(modifier = Modifier.height(8.dp))
                Button({ cameraPermissionState.launchPermissionRequest() }) {
                    Text("Разрешить доступ")
                }
            }
        }
    }
}

@Composable
fun QrScannerScreenUi(
    onEmailScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx -> PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } },
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context), QrCodeAnalyzer(onEmailScanned))
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analyzer)
            } catch (e: Exception) {
                Log.e("QrScanner", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}

class QrCodeAnalyzer(private val onEmailScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { email ->
                            onEmailScanned(email)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

@Serializable
data class QrPayload(
    val id: String,
    val token: String
)
