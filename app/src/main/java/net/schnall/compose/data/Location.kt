package net.schnall.compose.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity
data class Location(
    @PrimaryKey
    val name: String,
    val lat: Double,
    val lon: Double,
    val forecastUpdated: LocalDateTime
)
