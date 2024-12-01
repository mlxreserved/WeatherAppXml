package com.example.weatherappxml.ui.model

data class SearchUiState(
    val textFieldCity: String = "",
    val selectedCity: String = "",
    val coordinateList: List<String> = emptyList(),
)