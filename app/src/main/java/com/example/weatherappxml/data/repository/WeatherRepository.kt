package com.example.weatherappxml.data.repository

import com.example.weatherappxml.data.api.WeatherService
import com.example.weatherappxml.data.api.model.Weather
import javax.inject.Inject

private const val KEY_WEATHER = "e60480c2322946229e0112431240307"

interface WeatherRepository{
    suspend fun getWeather(city: String, language: String): Weather
}

class WeatherRepositoryImpl @Inject constructor(private val weatherService: WeatherService): WeatherRepository{

    override suspend fun getWeather(city: String, language: String): Weather =
        weatherService.getWeather(city = city, lang = language, key = KEY_WEATHER, days = "3")

}