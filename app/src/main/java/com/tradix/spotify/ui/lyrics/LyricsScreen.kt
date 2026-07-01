package com.tradix.spotify.ui.lyrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradix.spotify.data.models.LyricLine
import com.tradix.spotify.ui.theme.*

@Composable
fun LyricsScreen(
    lyrics: List<LyricLine>,
    currentPositionMs: Long,
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit
) {
    // Find current line index based on playback position
    val currentLineIndex = remember(currentPositionMs, lyrics) {
        var index = 0
        for (i in lyrics.indices) {
            if (lyrics[i].timestampMs <= currentPositionMs) {
                index = i
            } else {
                break
            }
        }
        index
    }

    val listState = rememberLazyListState()

    // Auto scroll to current line
    LaunchedEffect(currentLineIndex) {
        if (lyrics.isNotEmpty()) {
            listState.animateScrollToItem(
                index = (currentLineIndex - 2).coerceAtLeast(0)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PlayerGradientStart,
                        SpotifyBlack
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Back",
                        tint = SpotifyWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "LYRICS",
                    color = SpotifyWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SpotifyGreen)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "No lyrics found",
                                color = SpotifyWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lyrics aren't available for this track",
                                color = SpotifyLightGray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                lyrics.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No lyrics available",
                            color = SpotifyLightGray,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        )
                    ) {
                        itemsIndexed(lyrics) { index, line ->
                            LyricLineItem(
                                line = line,
                                isActive = index == currentLineIndex
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LyricLineItem(
    line: LyricLine,
    isActive: Boolean
) {
    Text(
        text = line.text,
        color = if (isActive) SpotifyWhite else SpotifyLightGray.copy(alpha = 0.5f),
        fontSize = if (isActive) 26.sp else 22.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        lineHeight = 36.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        textAlign = TextAlign.Start
    )
}