package net.schnall.compose.network.di

import kotlinx.serialization.json.Json
import net.schnall.compose.BuildConfig
import net.schnall.compose.network.WeatherApi
import net.schnall.compose.network.WeatherApiImpl
import net.schnall.compose.network.WeatherService
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.openweathermap.org/"
private const val OPEN_WEATHER_MAP_APP_ID = "f29c7c77f7f5de2caa22ad9c92e2fe54"

fun networkModule() = module {
    single<WeatherApi> { WeatherApiImpl(get()) }

    single<WeatherService> {
        val json = Json { ignoreUnknownKeys = true }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
            .create(WeatherService::class.java)
    }

    single<OkHttpClient> {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        // Add AppId param to every API call
        builder.addInterceptor(
            Interceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("appid", OPEN_WEATHER_MAP_APP_ID)
                    .addQueryParameter("units", "imperial")
                    .build()
                val request = chain.request().newBuilder()
                    .url(url)
                    .build()
                chain.proceed(request)
            }
        )

        builder.build()
    }
}
