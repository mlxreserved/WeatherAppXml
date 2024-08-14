package com.example.weatherappxml.data.repository

import com.example.weatherappxml.data.database.City
import com.example.weatherappxml.data.database.CityDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineCitiesRepository @Inject constructor(private val cityDao: CityDao): CitiesRepository {
    override fun delete() = cityDao.delete()

    override fun getAllCities(): Flow<List<City>> = cityDao.getAllCities()

    override fun insert(city: City) = cityDao.insert(city)
}