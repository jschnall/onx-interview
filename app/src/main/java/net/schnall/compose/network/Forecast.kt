package net.schnall.compose.network

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResult(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Forecast>,
    val message: Int
)

@Serializable
data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
)

@Serializable
data class Coord(
    val lat: Double,
    val lon: Double
)

@Serializable
data class Forecast(
    val clouds: Clouds,
    val dt: Long,
    val dt_txt: String,
    val main: Main,
    val pop: Double,
    val rain: Rain? = null,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_kf: Double,
    val temp_max: Double,
    val temp_min: Double
)

@Serializable
data class Sys(
    val pod: String
)

@Serializable
data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

@Serializable
data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)

@Serializable
data class Rain(
    val `3h`: Double
)