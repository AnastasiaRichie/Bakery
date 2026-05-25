package com.bakery_tm.bakery.screen

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bakery_tm.bakery.R
import com.bakery_tm.bakery.common.Primary
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
fun OrdersList(
    modifier: Modifier,
    orders: List<OrderResponse>,
    showScanButton: Boolean = false,
    onScanClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    onOrderReceived: (Long) -> Unit,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        if (orders.isEmpty()) {
            Column(modifier.fillMaxSize()) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Заказы отсуствуют",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                    items(orders, key = { it.orderId }) { order ->
                        OrderCard(
                            order = order,
                            shouldShowMarkButton = !showScanButton,
                            onMarkReceived = { onOrderReceived(it.orderId) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (!showScanButton) {
                    Button(onBackClicked, modifier = Modifier.fillMaxWidth()) {
                        Text("Назад")
                    }
                }
            }
        }
        if (showScanButton) {
            FloatingActionButton(onScanClicked, modifier = Modifier.padding(12.dp)) {
                Icon(painterResource(R.drawable.ic_scanner), contentDescription = null)
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderResponse, shouldShowMarkButton: Boolean, onMarkReceived: (OrderResponse) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Заказ #${order.orderId}", style = MaterialTheme.typography.titleMedium)
            if (shouldShowMarkButton) {
                Text("Адрес: ${order.address.city}, ${order.address.address}")
                Text("Дата: ${Date(order.date).toLocaleString()}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                order.items.forEach { item -> Text("${item.quantity} x ${item.product.name}") }
            }

            if (order.orderState == OrderState.ORDERED && shouldShowMarkButton) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onMarkReceived(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
                ) {
                    Text("Выдан")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(onEmailScanned: (String) -> Unit) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current
    when {
        cameraPermissionState.status.isGranted -> {
            QrScannerScreenUi {
                try {
                    val payload = Json.decodeFromString<QrPayload>(it)
                    onEmailScanned(payload.token)
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка при парсинге QR!", Toast.LENGTH_SHORT).show()
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    //modifier = Modifier.fillMaxSize(),
                    text = "Разрешение камеры не предоставлено. Для работы приложения разрешите доступ",
                    textAlign = TextAlign.Center
                )
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
