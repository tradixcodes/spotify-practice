package com.tradix.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tradix.spotify.ui.lyrics.LyricsViewModel
import com.tradix.spotify.ui.theme.*

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val track = uiState.currentTrack ?: return
    val lyricsViewModel: LyricsViewModel = viewModel()
    val lyricsUiState by lyricsViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showFullLyrics by remember { mutableStateOf(false) }

    // Find current lyric line
    val currentLineIndex = remember(uiState.currentPositionMs, lyricsUiState.lyrics) {
        var index = 0
        for (i in lyricsUiState.lyrics.indices) {
            if (lyricsUiState.lyrics[i].timestampMs <= uiState.currentPositionMs) {
                index = i
            } else break
        }
        index
    }

    // Load lyrics automatically when track changes
    LaunchedEffect(track.trackId) {
        lyricsViewModel.loadLyrics(
            artistName = track.artistName ?: "",
            trackName = track.trackName ?: "",
            albumName = track.collectionName,
            durationSecs = track.trackTimeMillis?.div(1000)?.toInt()
        )
    }

    // ← Wrap everything in a Box
    Box(modifier = Modifier.fillMaxSize()) {

        // Main player content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PlayerGradientStart,
                            PlayerGradientEnd
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Dismiss",
                            tint = SpotifyWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NOW PLAYING",
                            color = SpotifyLightGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = track.collectionName ?: "Single",
                            color = SpotifyWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = SpotifyWhite
                        )
                    }
                }

                // Album art
                AsyncImage(
                    model = track.artworkUrl100?.replace("100x100", "600x600"),
                    contentDescription = track.trackName,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Track info + like button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.trackName ?: "Unknown",
                            color = SpotifyWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artistName ?: "Unknown Artist",
                            color = SpotifyLightGray,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { viewModel.toggleLike() }) {
                        Icon(
                            imageVector = if (uiState.isLiked)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (uiState.isLiked) SpotifyGreen else SpotifyLightGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Single lyric line above progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    when {
                        lyricsUiState.isLoading -> {
                            CircularProgressIndicator(
                                color = SpotifyGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        lyricsUiState.lyrics.isNotEmpty() -> {
                            Text(
                                text = lyricsUiState.lyrics[currentLineIndex].text,
                                color = SpotifyWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        lyricsUiState.error != null -> {
                            Text(
                                text = "No lyrics available",
                                color = SpotifyLightGray.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                // Progress bar
                Slider(
                    value = uiState.progress,
                    onValueChange = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = SpotifyWhite,
                        activeTrackColor = SpotifyWhite,
                        inactiveTrackColor = SpotifyMediumGray
                    )
                )

                // Timestamps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(uiState.currentPositionMs),
                        color = SpotifyLightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatDuration(track.trackTimeMillis ?: 0L),
                        color = SpotifyLightGray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Playback controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleShuffle() }) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (uiState.isShuffled) SpotifyGreen else SpotifyLightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous",
                            tint = SpotifyWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SpotifyWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { viewModel.togglePlayPause() }) {
                            Icon(
                                imageVector = if (uiState.isPlaying)
                                    Icons.Filled.Pause
                                else
                                    Icons.Filled.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                tint = SpotifyBlack,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            tint = SpotifyWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleRepeat() }) {
                        Icon(
                            imageVector = Icons.Filled.Repeat,
                            contentDescription = "Repeat",
                            tint = if (uiState.isRepeating) SpotifyGreen else SpotifyLightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Lyrics card preview
                if (lyricsUiState.lyrics.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        onClick = { showFullLyrics = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LYRICS",
                                    color = SpotifyWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.OpenInFull,
                                    contentDescription = "Expand lyrics",
                                    tint = SpotifyLightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            val previewStart = (currentLineIndex - 1).coerceAtLeast(0)
                            val previewEnd = (currentLineIndex + 3)
                                .coerceAtMost(lyricsUiState.lyrics.size - 1)
                            lyricsUiState.lyrics
                                .subList(previewStart, previewEnd + 1)
                                .forEachIndexed { index, line ->
                                    val isActive = previewStart + index == currentLineIndex
                                    Text(
                                        text = line.text,
                                        color = if (isActive) SpotifyWhite
                                        else SpotifyLightGray.copy(alpha = 0.4f),
                                        fontSize = if (isActive) 16.sp else 13.sp,
                                        fontWeight = if (isActive) FontWeight.Bold
                                        else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "See full lyrics",
                                color = SpotifyLightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Full screen lyrics overlay — rendered OUTSIDE the scroll column
        if (showFullLyrics) {
            com.tradix.spotify.ui.lyrics.LyricsScreen(
                lyrics = lyricsUiState.lyrics,
                currentPositionMs = uiState.currentPositionMs,
                isLoading = lyricsUiState.isLoading,
                error = lyricsUiState.error,
                onDismiss = { showFullLyrics = false }
            )
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}