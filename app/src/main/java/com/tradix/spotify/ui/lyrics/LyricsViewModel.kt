package com.tradix.spotify.ui.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradix.spotify.data.models.LyricLine
import com.tradix.spotify.data.repository.LyricsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LyricsUiState(
    val isLoading: Boolean = false,
    val lyrics: List<LyricLine> = emptyList(),
    val error: String? = null
)

class LyricsViewModel : ViewModel() {

    private val repository = LyricsRepository()

    private val _uiState = MutableStateFlow(LyricsUiState())
    val uiState: StateFlow<LyricsUiState> = _uiState

    fun loadLyrics(
        artistName: String,
        trackName: String,
        albumName: String? = null,
        durationSecs: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.value = LyricsUiState(isLoading = true)

            val result = repository.getSyncedLyrics(
                artistName = artistName,
                trackName = trackName,
                albumName = albumName,
                durationSecs = durationSecs
            )

            result.fold(
                onSuccess = { lines ->
                    _uiState.value = LyricsUiState(lyrics = lines)
                },
                onFailure = { error ->
                    _uiState.value = LyricsUiState(error = error.message)
                }
            )
        }
    }
}