package net.schnall.compose.network

import kotlinx.serialization.Serializable

@Serializable
class LocationResult : ArrayList<Location>()

@Serializable
data class Location(
    val country: String?,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String?
)

@Serializable
data class LocalNames(
    val be: String,
    val cy: String,
    val en: String,
    val fr: String,
    val he: String,
    val ko: String,
    val mk: String,
    val ru: String
)