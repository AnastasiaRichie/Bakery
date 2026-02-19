package com.bakery_tm.bakery.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NetworkManager(context: Context, private val applicationScope: CoroutineScope) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableSharedFlow<Boolean>(replay = 1)
    val networkState = _networkState.asSharedFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val isConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            applicationScope.launch {
                _networkState.emit(isConnected)
            }
        }

        override fun onLost(network: Network) {
            applicationScope.launch {
                _networkState.emit(false)
            }
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        applicationScope.coroutineContext[Job]?.invokeOnCompletion {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
        applicationScope.launch {
            _networkState.emit(isCurrentInternetAvailable())
        }
    }

    fun isCurrentInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}
