package com.tradix.spotify.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tradix.spotify.data.models.PlaylistItem
import com.tradix.spotify.data.models.RecentlyPlayed
import com.tradix.spotify.ui.theme.*

// Keeping recently played as placeholder until we have user auth
val recentlyPlayedItems = listOf(
    RecentlyPlayed("1", "Liked Songs", "https://picsum.photos/seed/liked/200"),
    RecentlyPlayed("2", "Top Hits", "https://picsum.photos/seed/tophits/200"),
    RecentlyPlayed("3", "Chill Vibes", "https://picsum.photos/seed/chill/200"),
    RecentlyPlayed("4", "Afrobeats", "https://picsum.photos/seed/afro/200"),
)

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { HomeHeader() }

        item { RecentlyPlayedSection() }

        // Loading state
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SpotifyGreen)
                }
            }
        }

        // Error state
        uiState.errorMessage?.let { error ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Couldn't load content",
                        color = SpotifyWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = SpotifyLightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.retryLoading() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpotifyGreen
                        )
                    ) {
                        Text("Retry", color = SpotifyBlack)
                    }
                }
            }
        }
        if (uiState.hotTracks.isNotEmpty()) {
            item {
                PlaylistSection(
                    title = "🔥 Hot Right Now",
                    items = uiState.hotTracks
                )
            }
        }

        if (uiState.afrobeats.isNotEmpty()) {
            item {
                PlaylistSection(
                    title = "🎵 Afrobeats",
                    items = uiState.afrobeats
                )
            }
        }

        if (uiState.hiphop.isNotEmpty()) {
            item {
                PlaylistSection(
                    title = "🎤 Hip Hop",
                    items = uiState.hiphop
                )
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Good morning 👋",
            color = SpotifyWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = SpotifyWhite,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun RecentlyPlayedSection() {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = "Recently Played",
            color = SpotifyWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recentlyPlayedItems) { item ->
                RecentlyPlayedChip(item = item)
            }
        }
    }
}

@Composable
fun RecentlyPlayedChip(item: RecentlyPlayed) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(56.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.title,
                color = SpotifyWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun PlaylistSection(title: String, items: List<PlaylistItem>) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            color = SpotifyWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                PlaylistCard(item = item)
            }
        }
    }
}

@Composable
fun PlaylistCard(item: PlaylistItem) {
    Column(modifier = Modifier.width(150.dp)) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = SpotifyWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = item.subtitle,
            color = SpotifyLightGray,
            fontSize = 11.sp,
            maxLines = 2
        )
    }
}