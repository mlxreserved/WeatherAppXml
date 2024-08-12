package com.example.weatherappxml.data.repository

import com.example.weatherappxml.data.api.CoordinateService
import com.example.weatherappxml.data.api.model.Coordinate
import javax.inject.Inject

private const val KEY_COORDINATE = "8868527d-80b8-43d6-bab9-f6111ec94ee8"

interface CoordinateRepository{

    suspend fun getCoordinate(city: String, lang: String): Coordinate

}

class CoordinateRepositoryImpl @Inject constructor(private val coordinateService: CoordinateService): CoordinateRepository {

    override suspend fun getCoordinate(city: String, lang: String): Coordinate = coordinateService.getCoordinate(
        geocode = city,
        apiKey = KEY_COORDINATE,
        lang = lang
    )
}