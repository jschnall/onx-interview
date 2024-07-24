package net.schnall.compose.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface WeatherApi {
    fun fetchForecasts(location: String): Flow<List<Forecast>>
    fun fetchLocation(latitude: Double, longitude: Double, limit: Int = 1): Flow<Location?>
}

class WeatherApiImpl(private val weatherService: WeatherService): WeatherApi {
    override fun fetchForecasts(location: String) = flow {
        emit(weatherService.forecasts(location).list)
    }

    override fun fetchLocation(latitude: Double, longitude: Double, limit: Int) = flow {
        emit(weatherService.locations(latitude, longitude, limit).firstOrNull())
    }
}