package com.example.weatherappxml.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weatherappxml.data.api.model.Coordinate
import com.example.weatherappxml.data.api.model.Hour
import com.example.weatherappxml.data.api.model.Weather
import com.example.weatherappxml.data.database.City
import com.example.weatherappxml.data.repository.CitiesRepository
import com.example.weatherappxml.data.repository.CoordinateRepository
import com.example.weatherappxml.data.repository.WeatherRepository
import com.example.weatherappxml.di.MainApp
import com.example.weatherappxml.utils.WeatherResult
import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetector
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

data class SearchState(
    val textFieldCity: String = "",
    val selectedCity: String = "",
    val coordinateList: List<String> = emptyList(),
)


data class WeatherState(
    val result: WeatherResult = WeatherResult.Loading,
    val languageMap: Map<String, String> = mapOf("ru" to "ru_RU", "en" to "en_US"),
    val lang: String = "ru",
    val hourList: MutableList<Hour> = mutableListOf(),
    val currentItem: Int = -1
)

data class DatabaseState(
    val storyOfSearch: List<City> = listOf(),
)

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val coordinateRepository: CoordinateRepository,
    private val citiesRepository: CitiesRepository
): ViewModel() {


    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()


    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()


    val databaseState: StateFlow<DatabaseState> = citiesRepository.getAllCities().map { DatabaseState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS ),
            initialValue = DatabaseState()
        )



    init{
        getWeather("Москва", isReloading = true)
    }

    /*fun getWeather(city: String){
        viewModelScope.launch {
            _weatherState.update { it.copy(result = WeatherResult.Loading) }
            val result: WeatherResult = try {
                val weather = weatherRepository.getWeather(city)
                WeatherResult.Success(weather)
            } catch(e: Exception) {
                WeatherResult.Error("${e.message}")
            }
            _weatherState.update { it.copy(result = result) }
        }
    }*/

    fun getWeather(city: String, isReloading: Boolean){
        if (city.isNotBlank()) {
            getLanguage(city)
            viewModelScope.launch {
                _weatherState.update { it.copy(result = WeatherResult.Loading) }
                val res = try { // Попытка получить погоду
                    val (coordinateOfCity,nameCity, nameFullCity) = getCoordinate(city)
                    val weather =
                        weatherRepository.getWeather(coordinateOfCity, _weatherState.value.lang)
                    weather.location.name = nameCity
                    if(!isReloading) {
                        saveCity(
                            City(
                                name = nameFullCity,
                                coordinate = coordinateOfCity,
                                date = getCurrentTime()
                            )
                        )
                    }
                    addHourToHourList(weather)
                    closeSearchScreen()
                    WeatherResult.Success(weather)

                } catch (e: Exception) {
                    WeatherResult.Error("${e.message}")
                }
                _weatherState.update {
                    it.copy(
                        result = res
                    )
                }
            }
        }
    }

    fun closeSearchScreen(){
        _searchState.update { it.copy(coordinateList = emptyList()) }
        clearCity()
    }
    fun clearCity() {
        _searchState.update { it.copy(textFieldCity = "") }
        _searchState.update { it.copy(coordinateList = emptyList()) }
    }

    //Получение координат города
    private suspend fun getCoordinate(city: String): Triple<String,String, String> {
        try {
            val coordinate: Triple<String, String, String> = convertCoordinate(coordinateRepository.getCoordinate(city = city, _weatherState.value.languageMap[_weatherState.value.lang] ?: "ru_RU"))
            return coordinate
        } catch (e: IOException){
            return Triple("","","")
        }
    }

    private fun addHourToHourList(weather: Weather){
        _weatherState.update { it.copy(hourList = mutableListOf()) }
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date

        for(i in cal.get(Calendar.HOUR_OF_DAY) ..< weather.forecast.forecastday[0].hour.size) {
            _weatherState.value.hourList.add(weather.forecast.forecastday[0].hour[i])
        }
        for(i in 0..cal.get(Calendar.HOUR_OF_DAY)){
            _weatherState.value.hourList.add(weather.forecast.forecastday[1].hour[i])
        }
    }

    fun getCurrentTime(): Long{
        return Date().time
    }



    private fun convertCoordinate(res: Coordinate): Triple<String,String, String>{
        val position =
            res.response.GeoObjectCollection.featureMember[0].GeoObject.Point.pos.split(" ")
                .reversed().joinToString(",")
        val cityName = res.response.GeoObjectCollection.featureMember[0].GeoObject.name
        var cityFullName = res.response.GeoObjectCollection.featureMember[0].GeoObject.metaDataProperty.GeocoderMetaData.text
        val cityNameItems = cityFullName.split(", ").toMutableList()
        if(cityNameItems.size > 1) {
            cityNameItems.removeFirst()
        }
        cityFullName = cityNameItems.joinToString(", ")
        return Triple(position, cityName, cityFullName)
    }

    fun getMultiCoordinate(city: String) {
        if(city.isNotBlank()) {
            viewModelScope.launch {
                try {
                    getLanguage(city)
                    val res = coordinateRepository.getCoordinate(
                        city = city,
                        _weatherState.value.languageMap[_weatherState.value.lang] ?: "ru_RU"
                    )
                    _searchState.update { it.copy(coordinateList = handleNameOfCity(res)) }
                } catch (e: IOException) {
                    Log.e("Coordinate", "${e.message}")
                }
            }
        }
    }

    private fun handleNameOfCity(res: Coordinate): List<String>{
        val listOfCoordinates = mutableListOf<String>()
        for(i in res.response.GeoObjectCollection.featureMember){
            var cityName = i.GeoObject.metaDataProperty.GeocoderMetaData.text
            val cityNameItems = cityName.split(", ").toMutableList()
            if(cityNameItems.size > 1) {
                cityNameItems.removeFirst()
            }
            cityName = cityNameItems.joinToString(", ")
            listOfCoordinates.add(cityName)
        }
        return listOfCoordinates
    }

    private fun saveCity(city: City){
        viewModelScope.launch(Dispatchers.IO) {
            if(databaseState.value.storyOfSearch.size==10){
                citiesRepository.delete()
            }
            citiesRepository.insert(city)
        }
    }

    fun setCurrentItem(item: Int){
        _weatherState.update { it.copy(currentItem = item) }
    }

    //Определение языка
    fun getLanguage(city: String){
        val detector: LanguageDetector = LanguageDetectorBuilder.fromLanguages(Language.ENGLISH, Language.RUSSIAN).build()
        val detectedLanguage: Language = detector.detectLanguageOf(text = city)
        _weatherState.update { it.copy(lang = detectedLanguage.name.substring(0,2).lowercase()) }
    }

    fun formatDate(date: String, week: Boolean, short: Boolean): String{
        val dateFormatter = if(week) {
            if(short){
                SimpleDateFormat("E")
            } else {
                SimpleDateFormat("EEEE")
            }
        }
        else {
            if(short){
                SimpleDateFormat("dd")
            } else {
                SimpleDateFormat("dd MMMM")
            }
        }

        val formatedDate = dateFormatter.format(SimpleDateFormat("yyyy-MM-dd").parse(date))

        return formatedDate

    }

    fun updateSearchText(input: String){
        _searchState.update {
            it.copy(
                textFieldCity = input
            )
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MainApp)
                val weatherRepository = application.appComponent.weatherRepository
                val coordinateRepository = application.appComponent.coordinateRepository
                val citiesRepository = application.databaseComponent.citiesRepository
                WeatherViewModel(
                    weatherRepository = weatherRepository,
                    coordinateRepository = coordinateRepository,
                    citiesRepository = citiesRepository
                )
            }
        }
    }
}