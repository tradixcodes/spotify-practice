package com.tradix.spotify.data.models

import com.google.gson.annotations.SerializedName

// ─── UI Models (what our screens use) ───────────────────────────────

data class PlaylistItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

data class RecentlyPlayed(
    val id: String,
    val title: String,
    val imageUrl: String
)

// ─── Spotify API Models (what the API returns) ──────────────────────

data class SpotifyFeaturedPlaylists(
    val playlists: SpotifyPlaylistPage
)

data class SpotifyPlaylistPage(
    val items: List<SpotifyPlaylist>
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val description: String,
    val images: List<SpotifyImage>,
    val tracks: SpotifyTracksSummary?
)

data class SpotifyImage(
    val url: String,
    val width: Int?,
    val height: Int?
)

data class SpotifyTracksSummary(
    val total: Int
)

data class SpotifySearchResponse(
    val tracks: SpotifyTrackPage?,
    val artists: SpotifyArtistPage?,
    val albums: SpotifyAlbumPage?
)

data class SpotifyTrackPage(
    val items: List<SpotifyTrack>
)

data class SpotifyArtistPage(
    val items: List<SpotifyArtist>
)

data class SpotifyAlbumPage(
    val items: List<SpotifyAlbum>
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    @SerializedName("duration_ms") val durationMs: Long,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum?,
    @SerializedName("preview_url") val previewUrl: String?
)

data class SpotifyArtist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>?,
    val genres: List<String>?,
    val followers: SpotifyFollowers?
)

data class SpotifyFollowers(
    val total: Int
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val artists: List<SpotifyArtist>,
    @SerializedName("release_date") val releaseDate: String?,
    val tracks: SpotifyTrackPage?
)

// ─── Auth Models ─────────────────────────────────────────────────────

data class SpotifyTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// New Releases
data class SpotifyNewReleases(
    val albums: SpotifyAlbumPage
)

// Categories
data class SpotifyCategories(
    val categories: SpotifyCategoryPage
)

data class SpotifyCategoryPage(
    val items: List<SpotifyCategory>
)

data class SpotifyCategory(
    val id: String,
    val name: String,
    val icons: List<SpotifyImage>
)

// iTunes Models
data class ItunesSearchResponse(
    val resultCount: Int,
    val results: List<ItunesTrack>
)

data class ItunesTrack(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    val primaryGenreName: String?,
    val trackTimeMillis: Long?
)