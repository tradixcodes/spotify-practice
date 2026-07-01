package com.tradix.spotify.data.repository

import com.tradix.spotify.data.api.RetrofitClient
import com.tradix.spotify.data.models.ItunesTrack

class MelodifyRepository {

    private val api = RetrofitClient.melodifyApi

    suspend fun search(query: String): Result<List<ItunesTrack>> {
        return try {
            val response = api.search(query)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchByGenre(genre: String): Result<List<ItunesTrack>> {
        return try {
            val response = api.searchByGenre(genre)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}