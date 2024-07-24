package net.schnall.compose.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/forecast")
    suspend fun forecasts(@Query("q") location: String): ForecastResult

    @GET("geo/1.0/reverse")
    suspend fun locations(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int
    ): LocationResult
}
