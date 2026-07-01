package com.tradix.spotify.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MelodifyPlayer(context: Context) {

    val exoPlayer = ExoPlayer.Builder(context).build()

    fun playTrack(previewUrl: String) {
        val mediaItem = MediaItem.fromUri(previewUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun resume() {
        exoPlayer.play()
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun release() {
        exoPlayer.release()
    }

    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    fun getDuration(): Long {
        return exoPlayer.duration.coerceAtLeast(0L)
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun addListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        exoPlayer.removeListener(listener)
    }

    val isPlaying: Boolean get() = exoPlayer.isPlaying
}