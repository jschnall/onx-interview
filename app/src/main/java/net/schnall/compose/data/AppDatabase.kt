package net.schnall.compose.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.schnall.compose.data.dao.LocationDao
import net.schnall.compose.data.dao.WeatherDao

@Database(entities = [Weather::class, Location::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun weatherDao(): WeatherDao
}