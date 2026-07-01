package com.tradix.spotify.data.api

import com.tradix.spotify.data.models.LrcLibResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LrcLibApi {

    @GET("api/get")
    suspend fun getLyrics(
        @Query("artist_name") artistName: String,
        @Query("track_name") trackName: String,
        @Query("album_name") albumName: String? = null,
        @Query("duration") duration: Int? = null
    ): LrcLibResponse
}