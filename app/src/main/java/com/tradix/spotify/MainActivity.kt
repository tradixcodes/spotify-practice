package com.tradix.spotify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tradix.spotify.ui.components.BottomNavBar
import com.tradix.spotify.ui.components.BottomNavItem
import com.tradix.spotify.ui.home.HomeScreen
import com.tradix.spotify.ui.library.LibraryScreen
import com.tradix.spotify.ui.player.PlayerScreen
import com.tradix.spotify.ui.player.PlayerViewModel
import com.tradix.spotify.ui.search.SearchScreen
import com.tradix.spotify.ui.theme.SpotifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpotifyTheme {
                MelodifyApp()
            }
        }
    }
}

@Composable
fun MelodifyApp() {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = viewModel()
    val playerUiState by playerViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None}
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(playerViewModel = playerViewModel)
                }
                composable(BottomNavItem.Search.route) {
                    SearchScreen()
                }
                composable(BottomNavItem.Library.route) {
                    LibraryScreen()
                }
            }
        }

        // Full screen player overlay
        AnimatedVisibility(
            visible = playerUiState.showPlayer,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            PlayerScreen(
                viewModel = playerViewModel,
                onDismiss = { playerViewModel.hidePlayer() }
            )
        }
    }
}
