package com.tradix.spotify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Library
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.LibraryMusic

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        label = "Home",
        icon = Icons.Filled.Home
    )
    object Search : BottomNavItem(
        route = "search",
        label = "Search",
        icon = Icons.Filled.Search
    )
    object Library : BottomNavItem(
        route = "library",
        label = "Your Library",
        icon = Icons.Filled.LibraryMusic
    )
}