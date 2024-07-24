package net.schnall.compose.repo.di

import androidx.room.Room
import kotlinx.datetime.Clock
import net.schnall.compose.data.AppDatabase
import net.schnall.compose.data.dao.LocationDao
import net.schnall.compose.data.dao.WeatherDao
import net.schnall.compose.network.di.networkModule
import net.schnall.compose.repo.WeatherRepo
import net.schnall.compose.repo.WeatherRepoImpl
import org.koin.dsl.module

val repoModule = module {
    includes(networkModule())

    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "GameDatabase"
        ).build()
    }
    single<LocationDao> { get<AppDatabase>().locationDao() }
    single<WeatherDao> { get<AppDatabase>().weatherDao() }
    single<WeatherRepo> { WeatherRepoImpl(get(), get(), get(), get()) }
    single<Clock> { Clock.System }
}