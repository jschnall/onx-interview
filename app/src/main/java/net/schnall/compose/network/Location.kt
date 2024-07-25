package net.schnall.compose.network

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val country: String?,
    val lat: Double,
    // val local_names: LocalNames? = null,
    val lon: Double,
    val name: String,
    val state: String?
)

//@Serializable
//data class LocalNames(
//    val be: String? = null,
//    val cy: String? = null,
//    val en: String? = null,
//    val fr: String? = null,
//    val he: String? = null,
//    val ko: String? = null,
//    val mk: String? = null,
//    val ru: String? = null
//)