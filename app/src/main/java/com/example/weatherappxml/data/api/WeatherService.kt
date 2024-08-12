package com.example.weatherappxml.data.api

import com.example.weatherappxml.data.api.model.Weather
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {
    @GET("forecast.json")
    suspend fun getWeather(@Query("q") city: String,
                           @Query("lang") lang: String,
                           @Query("key") key: String,
                           @Query("days") days: String): Weather
}