package com.tradix.spotify.data.api

import android.util.Base64
import com.tradix.spotify.BuildConfig
import com.tradix.spotify.data.models.SpotifyTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class SpotifyAuthService {

    private val client = OkHttpClient()

    suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val credentials = "${BuildConfig.SPOTIFY_CLIENT_ID}:${BuildConfig.SPOTIFY_CLIENT_SECRET}"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .header("Authorization", "Basic $encoded")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty response")

        android.util.Log.d("SpotifyAuth", "Token response: $body")

        val gson = com.google.gson.Gson()
        val tokenResponse = gson.fromJson(body, SpotifyTokenResponse::class.java)
        tokenResponse.accessToken
    }
}