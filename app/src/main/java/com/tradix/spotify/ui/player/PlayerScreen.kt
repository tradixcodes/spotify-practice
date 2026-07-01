package com.tradix.spotify.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tradix.spotify.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tradix.spotify.ui.lyrics.LyricsScreen
import com.tradix.spotify.ui.lyrics.LyricsViewModel

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val track = uiState.currentTrack ?: return
    val lyricsViewModel: LyricsViewModel = viewModel()
    val lyricsUiState by lyricsViewModel.uiState.collectAsState()
    var showLyrics by remember { mutableStateOf(false) }

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 32.dp),
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
                    .size(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

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

            // Time stamps
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
                // Shuffle
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (uiState.isShuffled) SpotifyGreen else SpotifyLightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        tint = SpotifyWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play/Pause
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

                // Next
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        tint = SpotifyWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat
                IconButton(onClick = { viewModel.toggleRepeat() }) {
                    Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        tint = if (uiState.isRepeating) SpotifyGreen else SpotifyLightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    if (!showLyrics) {
                        lyricsViewModel.loadLyrics(
                            artistName = track.artistName ?: "",
                            trackName = track.trackName ?: "",
                            albumName = track.collectionName,
                            durationSecs = track.trackTimeMillis?.div(1000)?.toInt()
                        )
                    }
                    showLyrics = true
                },
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(SpotifyGreen, SpotifyGreen))
                ),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "Lyrics",
                    tint = SpotifyGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lyrics",
                    color = SpotifyGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lyrics overlay
            if (showLyrics) {
                LyricsScreen(
                    lyrics = lyricsUiState.lyrics,
                    currentPositionMs = uiState.currentPositionMs,
                    isLoading = lyricsUiState.isLoading,
                    error = lyricsUiState.error,
                    onDismiss = { showLyrics = false }
                )
            }

        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}