package com.tradix.spotify.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.tradix.spotify.data.models.ItunesTrack
import com.tradix.spotify.player.MelodifyPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentTrack: ItunesTrack? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPositionMs: Long = 0L,
    val isLiked: Boolean = false,
    val isShuffled: Boolean = false,
    val isRepeating: Boolean = false,
    val showPlayer: Boolean = false
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val player = MelodifyPlayer(application)
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState
    private var progressJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                if (isPlaying) startProgressTracking() else stopProgressTracking()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _uiState.value = _uiState.value.copy(
                        isPlaying = false,
                        progress = 0f,
                        currentPositionMs = 0L
                    )
                }
            }
        })
    }

    fun playTrack(track: ItunesTrack) {
        _uiState.value = _uiState.value.copy(
            currentTrack = track,
            isPlaying = true,
            progress = 0f,
            currentPositionMs = 0L,
            showPlayer = true
        )
        track.previewUrl?.let { player.playTrack(it) }
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.resume()
        _uiState.value = _uiState.value.copy(isPlaying = player.isPlaying)
    }

    fun toggleLike() {
        _uiState.value = _uiState.value.copy(isLiked = !_uiState.value.isLiked)
    }

    fun toggleShuffle() {
        _uiState.value = _uiState.value.copy(isShuffled = !_uiState.value.isShuffled)
    }

    fun toggleRepeat() {
        _uiState.value = _uiState.value.copy(isRepeating = !_uiState.value.isRepeating)
    }

    fun seekTo(progress: Float) {
        val duration = player.getDuration()
        if (duration > 0) {
            val positionMs = (progress * duration).toLong()
            player.seekTo(positionMs)
            _uiState.value = _uiState.value.copy(
                progress = progress,
                currentPositionMs = positionMs
            )
        }
    }

    fun hidePlayer() {
        player.pause()
        _uiState.value = _uiState.value.copy(
            showPlayer = false,
            isPlaying = false
        )
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val position = player.getCurrentPosition()
                val duration = player.getDuration()
                if (duration > 0) {
                    _uiState.value = _uiState.value.copy(
                        progress = position.toFloat() / duration.toFloat(),
                        currentPositionMs = position
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        player.release()
    }
}