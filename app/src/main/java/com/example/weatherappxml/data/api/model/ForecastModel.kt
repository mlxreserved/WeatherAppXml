package com.example.weatherappxml.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val forecastday: List<Forecastday>
)

@Serializable
data class Forecastday(
    val date: String,
    val date_epoch: Int,
    val day: DayModel,
    val hour: List<Hour>
)