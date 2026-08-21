package com.example.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeFireApiService {
    @GET("api/v1/player-profile")
    suspend fun getPlayerProfile(
        @Query("uid") uid: String,
        @Query("server") server: String,
        @Query("region") region: String? = null
    ): Response<ResponseBody>

    @GET("info")
    suspend fun getPlayerInfoLegacy(
        @Query("uid") uid: String,
        @Query("server") server: String
    ): Response<ResponseBody>

    @GET("api/v1/search-players")
    suspend fun searchPlayers(
        @Query("keyword") keyword: String,
        @Query("server") server: String
    ): Response<ResponseBody>
}
