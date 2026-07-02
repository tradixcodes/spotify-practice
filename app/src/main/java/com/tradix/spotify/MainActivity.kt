package com.tradix.spotify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tradix.spotify.ui.components.PhoneLayout
import com.tradix.spotify.ui.components.TabletLayout
import com.tradix.spotify.ui.components.isTablet
import com.tradix.spotify.ui.player.PlayerViewModel
import com.tradix.spotify.ui.theme.SpotifyTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpotifyTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                MelodifyApp(isTablet = windowSizeClass.isTablet())
            }
        }
    }
}

@Composable
fun MelodifyApp(isTablet: Boolean = false) {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = viewModel()
    val playerUiState by playerViewModel.uiState.collectAsState()

    if (isTablet) {
        TabletLayout(
            navController = navController,
            playerViewModel = playerViewModel
        )
    } else {
        PhoneLayout(
            navController = navController,
            playerViewModel = playerViewModel,
            playerUiState = playerUiState
        )
    }
}