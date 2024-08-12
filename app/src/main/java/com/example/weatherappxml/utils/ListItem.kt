package com.example.weatherappxml.utils

import com.example.weatherappxml.data.api.model.Forecastday
import com.example.weatherappxml.data.api.model.Hour
import com.example.weatherappxml.data.api.model.Weather

sealed class ListItem{
    data class WeatherItem(val currentWeather: Weather): ListItem()
    data class DayItem(val forecast: Forecastday): ListItem()
    data class HourItem(val hour: Hour): ListItem()
    data class StringItem(val string: String): ListItem()
}