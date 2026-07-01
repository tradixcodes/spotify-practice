package com.tradix.spotify.data.repository

import com.tradix.spotify.data.api.RetrofitClient
import com.tradix.spotify.data.models.LyricLine

class LyricsRepository {

    private val api = RetrofitClient.lrcLibApi

    suspend fun getSyncedLyrics(
        artistName: String,
        trackName: String,
        albumName: String? = null,
        durationSecs: Int? = null
    ): Result<List<LyricLine>> {
        return try {
            val response = api.getLyrics(
                artistName = artistName,
                trackName = trackName,
                albumName = albumName,
                duration = durationSecs
            )

            if (response.syncedLyrics != null) {
                Result.success(parseLrc(response.syncedLyrics))
            } else if (response.plainLyrics != null) {
                // Fall back to plain lyrics with no timestamps
                val lines = response.plainLyrics.lines().mapIndexed { index, line ->
                    LyricLine(timestampMs = index * 3000L, text = line)
                }
                Result.success(lines)
            } else {
                Result.failure(Exception("No lyrics found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseLrc(lrc: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val pattern = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""")

        lrc.lines().forEach { line ->
            val match = pattern.find(line) ?: return@forEach
            val (minutes, seconds, centiseconds, text) = match.destructured
            val ms = minutes.toLong() * 60_000 +
                    seconds.toLong() * 1_000 +
                    centiseconds.toLong() * if (centiseconds.length == 2) 10 else 1
            if (text.trim().isNotEmpty()) {
                lines.add(LyricLine(timestampMs = ms, text = text.trim()))
            }
        }

        return lines.sortedBy { it.timestampMs }
    }
}