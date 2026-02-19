package com.bakery_tm.bakery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.bakery_tm.bakery.common.NetworkManager
import com.bakery_tm.bakery.common.collectWithLifecycle
import com.bakery_tm.bakery.ui.theme.BakeryTheme
import com.bakery_tm.bakery.view_model.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private var manager: NetworkManager? = null
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = NetworkManager(this.applicationContext, lifecycleScope)
        enableEdgeToEdge()
        lifecycleScope.launch {
            launch {
                manager?.networkState?.collect {
                    setContent {
                        BakeryTheme {
                            AppNavigation(manager?.isCurrentInternetAvailable() == true)
                        }
                    }
                }
            }
            launch {
                mainViewModel.user.collectWithLifecycle(this@MainActivity) { user ->
                    Log.e("qwe", "onCreate user: " + user)
                    if (user != null) {
                        mainViewModel.connect(user.userId)
                    } else {
                        mainViewModel.disconnect()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
}
