package com.example.weatherappxml.ui.model

import com.example.weatherappxml.data.api.model.Hour
import com.example.weatherappxml.utils.WeatherResult

data class WeatherUiState(
    val result: WeatherResult = WeatherResult.Loading,
    val languageMap: Map<String, String> = mapOf("ru" to "ru_RU", "en" to "en_US"),
    val lang: String = "ru",
    val hourList: MutableList<Hour> = mutableListOf(),
    val currentItem: Int = -1
)
