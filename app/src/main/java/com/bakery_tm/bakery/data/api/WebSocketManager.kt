package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.common.UpdateOrderListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class WebSocketManager(private val okHttpClient: OkHttpClient) {

    private var webSocket: WebSocket? = null
    private var updateOrderListener: UpdateOrderListener? = null

    fun connect(userId: Int) {
        if (webSocket != null) return
        val request = Request.Builder().url("ws://192.168.144.158:8080/api/orders/$userId").build()
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
        this.updateOrderListener = listener
    }

    fun detachOrderListener() {
        this.updateOrderListener = null
    }

    private fun parseWebSocketMessage(text: String) {
        try {
            val jsonObject = JSONObject(text)
            when (jsonObject.optString("type")) {
                ORDER_RECEIVED -> {
                    val rawPayload = jsonObject.optLong(ORDER_ID)
                    updateOrderListener?.requireOrderUpdate(rawPayload)
                }
                else -> {}
            }
        } catch (e: Exception) {
            "Failed to parse message: ${e.message}"
        }
    }

    private companion object {
        const val ORDER_RECEIVED = "ORDER_RECEIVED"
        const val ORDER_ID = "orderId"
    }
}