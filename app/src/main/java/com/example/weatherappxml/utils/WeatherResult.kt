package com.example.weatherappxml.utils

import com.example.weatherappxml.data.api.model.Weather

sealed interface WeatherResult {
    data class Success(val data: Weather): WeatherResult
    object Loading: WeatherResult
    data class Error(val errorMessage: String): WeatherResult

}