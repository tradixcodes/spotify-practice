package com.tradix.spotify.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MelodifyColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = SpotifyBlack,
    primaryContainer = SpotifyGreenDark,
    onPrimaryContainer = SpotifyWhite,
    secondary = SpotifyLightGray,
    onSecondary = SpotifyBlack,
    background = SpotifyBlack,
    onBackground = SpotifyWhite,
    surface = SurfaceColor,
    onSurface = SpotifyWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = SpotifyLightGray,
    error = ErrorColor,
    onError = SpotifyWhite
)

@Composable
fun SpotifyTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SpotifyBlack.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = MelodifyColorScheme,
        typography = MelodifyTypography,
        content = content
    )
}