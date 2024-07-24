package net.schnall.compose.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import net.schnall.compose.data.Weather

@Dao
interface WeatherDao {
    @Upsert
    suspend fun upsertAll(vararg weather: Weather)

    @Query("DELETE FROM $TABLE_NAME WHERE location = :location")
    suspend fun deleteByLocation(location: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<Weather>>

    @Query("SELECT * FROM $TABLE_NAME WHERE location = :location")
    fun getByLocation(location: String): Flow<List<Weather>>

    companion object {
        const val TABLE_NAME = "weather"
    }
}