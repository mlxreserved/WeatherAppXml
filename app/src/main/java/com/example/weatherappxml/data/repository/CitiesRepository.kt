package com.example.weatherappxml.data.repository

import com.example.weatherappxml.data.database.City
import kotlinx.coroutines.flow.Flow

interface CitiesRepository {
    fun getAllCities(): Flow<List<City>>

    fun delete()

    fun insert(city: City)
}