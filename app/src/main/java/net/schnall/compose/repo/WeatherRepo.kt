package net.schnall.compose.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import net.schnall.compose.data.Location
import net.schnall.compose.data.Weather
import net.schnall.compose.data.dao.LocationDao
import net.schnall.compose.data.dao.WeatherDao
import net.schnall.compose.network.WeatherApi

interface WeatherRepo {
    fun fetchForecast(locationName: String, forceRefresh: Boolean = false): Flow<List<Weather>>
    fun fetchLocation(lat: Double, lon: Double): Flow<Location?>
    fun listLocations(): Flow<List<Location>>
    suspend fun clearLocations()
}

class WeatherRepoImpl(
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,
    private val weatherApi: WeatherApi,
    private val clock: Clock
): WeatherRepo {
    override fun fetchForecast(locationName: String, forceRefresh: Boolean): Flow<List<Weather>> {
        return flow {
            locationDao.getByName(locationName).collect { location ->
                if (forceRefresh || isForecastExpired(location)) {
                    weatherApi.fetchForecasts(location.name).flowOn(Dispatchers.IO).collect { forecasts ->
                        weatherDao.deleteByLocation(locationName)
                        val weatherList = forecasts.map { forecast ->
                            Weather(
                                location = locationName,
                                dateTime = Instant.fromEpochSeconds(forecast.dt).toLocalDateTime(
                                    TimeZone.currentSystemDefault()),
                                description = forecast.weather.firstOrNull()?.description ?: "",
                                icon = forecast.weather.firstOrNull()?.icon ?: "",
                                windDirection = forecast.wind.deg,
                                windSpeed = forecast.wind.speed,
                                tempMin = forecast.main.temp_min.toInt(),
                                tempMax = forecast.main.temp_max.toInt(),
                            )
                        }
                        weatherDao.upsertAll(*weatherList.toTypedArray())
                        locationDao.upsertAll(location.copy(forecastUpdated = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())))
                        emit(weatherList)
                    }
                } else {
                    weatherDao.getByLocation(locationName).flowOn(Dispatchers.IO).collect { weatherList ->
                        emit(weatherList)
                    }
                }
            }
        }
    }

    override fun fetchLocation(lat: Double, lon: Double) = flow {
        weatherApi.fetchLocation(lat, lon).flowOn(Dispatchers.IO).collect { location ->
            location?.let {
                val entry = Location(
                    lat = lat,
                    lon = lon,
                    name = buildName(location),
                    forecastUpdated = Instant.fromEpochSeconds(0).toLocalDateTime(TimeZone.currentSystemDefault())
                )
                locationDao.upsertAll(entry)
                emit(entry)
            } ?: run {
                emit(null)
            }
        }
    }

    private fun buildName(location: net.schnall.compose.network.Location): String {
        val builder = StringBuilder()
        builder.append(location.name)
        location.state?.let {
            builder.append(",").append(it)
        }
        location.country?.let {
            builder.append(",").append(it)
        }
        return builder.toString()
    }

    override fun listLocations() = locationDao.getAll()
    override suspend fun clearLocations() {
        locationDao.deleteAll()
    }

    fun isForecastExpired(location: Location): Boolean {
        return clock.now().epochSeconds - location.forecastUpdated.toInstant(TimeZone.currentSystemDefault()).epochSeconds > ONE_DAY
    }

    companion object {
        const val ONE_DAY = 3600 * 24 // seconds
    }
}