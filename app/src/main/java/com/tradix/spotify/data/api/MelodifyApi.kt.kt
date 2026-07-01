package com.tradix.spotify.data.api

import com.tradix.spotify.data.models.ItunesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MelodifyApi {

    @GET("search")
    suspend fun search(
        @Query("term") query: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int = 20
    ): ItunesSearchResponse

    @GET("search")
    suspend fun searchByGenre(
        @Query("term") genre: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ): ItunesSearchResponse
}