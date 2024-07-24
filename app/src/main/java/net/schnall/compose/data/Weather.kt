package net.schnall.compose.data

import androidx.room.Entity
import kotlinx.datetime.LocalDateTime

@Entity(primaryKeys = ["location", "dateTime"])
data class Weather(
    val location: String,
    val dateTime: LocalDateTime,
    val description: String,
    val icon: String,
    val windDirection: Int, // degrees
    val windSpeed: Double, // meters per second
    val tempMin: Int,
    val tempMax: Int
)
