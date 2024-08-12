package com.example.weatherappxml.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)