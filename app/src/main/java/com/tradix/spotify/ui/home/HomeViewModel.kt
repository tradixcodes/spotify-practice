package com.tradix.spotify.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradix.spotify.data.models.PlaylistItem
import com.tradix.spotify.data.repository.MelodifyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val hotTracks: List<PlaylistItem> = emptyList(),
    val afrobeats: List<PlaylistItem> = emptyList(),
    val hiphop: List<PlaylistItem> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    private val repository = MelodifyRepository()
    private val trackCache = mutableMapOf<String, com.tradix.spotify.data.models.ItunesTrack>()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val hotDeferred = async { repository.searchByGenre("top hits") }
                val afroDeferred = async { repository.searchByGenre("afrobeats") }
                val hipHopDeferred = async { repository.searchByGenre("hiphop") }

                val hotResult = hotDeferred.await()
                val afroResult = afroDeferred.await()
                val hipHopResult = hipHopDeferred.await()

                // Cache all tracks for lookup
                hotResult.getOrNull()?.forEach { track ->
                    track.trackId?.let { trackCache[it.toString()] = track }
                }
                afroResult.getOrNull()?.forEach { track ->
                    track.trackId?.let { trackCache[it.toString()] = track }
                }
                hipHopResult.getOrNull()?.forEach { track ->
                    track.trackId?.let { trackCache[it.toString()] = track }
                }
                android.util.Log.d("HomeViewModel", "Hot result: ${hotResult.isSuccess}")
                android.util.Log.d("HomeViewModel", "Hot error: ${hotResult.exceptionOrNull()?.message}")
                android.util.Log.d("HomeViewModel", "Hot tracks: ${hotResult.getOrNull()?.size}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hotTracks = hotResult.getOrNull()?.mapNotNull { it.toPlaylistItem() } ?: emptyList(),
                    afrobeats = afroResult.getOrNull()?.mapNotNull { it.toPlaylistItem() } ?: emptyList(),
                    hiphop = hipHopResult.getOrNull()?.mapNotNull { it.toPlaylistItem() } ?: emptyList(),
                    errorMessage = null
                )
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun getTrackById(id: String): com.tradix.spotify.data.models.ItunesTrack? {
        return trackCache[id]
    }

    fun retryLoading() {
        loadHomeData()
    }
}

fun com.tradix.spotify.data.models.ItunesTrack.toPlaylistItem(): PlaylistItem? {
    if (trackName == null || artistName == null) return null
    return PlaylistItem(
        id = trackId?.toString() ?: "",
        title = trackName,
        subtitle = artistName,
        imageUrl = artworkUrl100?.replace("100x100", "300x300") ?: ""
    )
}