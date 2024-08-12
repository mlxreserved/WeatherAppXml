package com.example.weatherappxml.data.api

import com.example.weatherappxml.data.api.model.Coordinate
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query


interface CoordinateService {

    // return coordinate
    @GET("1.x/")
    suspend fun getCoordinate(
        @Query("geocode") geocode: String,
        @Query("apikey") apiKey: String,
        @Query("lang") lang: String,
        @Query("format") format: String = "json"
    ): Coordinate
}

