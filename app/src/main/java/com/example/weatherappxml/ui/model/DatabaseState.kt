package com.example.weatherappxml.ui.model

import com.example.weatherappxml.data.database.City

data class DatabaseState(
    val storyOfSearch: List<City> = listOf(),
)
