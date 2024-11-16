package com.example.weatherappxml.data.repository

import com.example.weatherappxml.data.api.CoordinateService
import com.example.weatherappxml.data.api.model.Coordinate
import javax.inject.Inject

private const val KEY_COORDINATE = "3fc8d508-f3bf-4404-8f1f-318cf75711fa"

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