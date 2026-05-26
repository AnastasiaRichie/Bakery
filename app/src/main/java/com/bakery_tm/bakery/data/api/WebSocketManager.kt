package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.common.UpdateOrderListener
import com.bakery_tm.bakery.di.BASE_DOMAIN
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArraySet

class WebSocketManager(private val okHttpClient: OkHttpClient) {

    private var webSocket: WebSocket? = null
    private val updateOrderListeners = CopyOnWriteArraySet<UpdateOrderListener>()

    fun connect(userId: Int) {
        if (webSocket != null) return
        val request = Request.Builder().url("ws://$BASE_DOMAIN/api/orders/$userId").build()
        webSocket = okHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) { parseWebSocketMessage(text) }
                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    this@WebSocketManager.webSocket = null
                }
                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    this@WebSocketManager.webSocket = null
                }
            }
        )
    }

    fun disconnect() {
        webSocket?.close(1000, "Closed by client")
        webSocket = null
    }

    fun attachOrderListener(listener: UpdateOrderListener) {
        updateOrderListeners.add(listener)
    }

    fun detachOrderListener(listener: UpdateOrderListener) {
        updateOrderListeners.remove(listener)
    }

    private fun parseWebSocketMessage(text: String) {
        try {
            val jsonObject = JSONObject(text)
            when (jsonObject.optString("type")) {
                ORDER_RECEIVED -> {
                    val rawPayload = jsonObject.optLong(ORDER_ID)
                    updateOrderListeners.forEach { it.requireOrderUpdate(rawPayload) }
                }
                ORDER_CREATED -> {
                    updateOrderListeners.forEach { it.onOrderCreated() }
                }
                else -> Unit
            }
        } catch (_: Exception) {
        }
    }

    private companion object {
        const val ORDER_RECEIVED = "ORDER_RECEIVED"
        const val ORDER_CREATED = "ORDER_CREATED"
        const val ORDER_ID = "orderId"
    }
}