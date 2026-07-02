package com.tradix.spotify.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tradix.spotify.ui.home.HomeScreen
import com.tradix.spotify.ui.library.LibraryScreen
import com.tradix.spotify.ui.player.PlayerScreen
import com.tradix.spotify.ui.player.PlayerUiState
import com.tradix.spotify.ui.player.PlayerViewModel
import com.tradix.spotify.ui.search.SearchScreen
import com.tradix.spotify.ui.theme.SpotifyBlack
import com.tradix.spotify.ui.theme.SpotifyLightGray
import com.tradix.spotify.ui.theme.SpotifyMediumGray

fun WindowSizeClass.isTablet(): Boolean {
    return widthSizeClass == WindowWidthSizeClass.Expanded ||
            widthSizeClass == WindowWidthSizeClass.Medium
}

@Composable
fun PhoneLayout(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    playerUiState: PlayerUiState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = { BottomNavBar(navController = navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
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

        // Full screen player overlay for phone
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

@Composable
fun TabletLayout(
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    val playerUiState by playerViewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {

        // Left pane — navigation + screens (60% width)
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
        ) {
            Scaffold(
                bottomBar = { BottomNavBar(navController = navController) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = BottomNavItem.Home.route,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
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
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(SpotifyMediumGray.copy(alpha = 0.3f))
        )

        // Right pane — player always visible (40% width)
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(SpotifyBlack)
        ) {
            if (playerUiState.currentTrack != null) {
                PlayerScreen(
                    viewModel = playerViewModel,
                    onDismiss = { }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Select a track to play",
                        color = SpotifyLightGray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}