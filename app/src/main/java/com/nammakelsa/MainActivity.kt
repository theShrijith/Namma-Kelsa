package com.nammakelsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nammakelsa.navigation.AppNavigation
import com.nammakelsa.ui.theme.NammaKelsaTheme
import com.nammakelsa.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Shared ViewModel — holds isDarkMode state
            val appViewModel: AppViewModel = viewModel()

            NammaKelsaTheme(darkTheme = appViewModel.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = appViewModel)
                }
            }
        }
    }
}
