package net.schnall.compose.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import net.schnall.compose.data.Location
import net.schnall.compose.network.Weather

@Dao
interface LocationDao {
    @Upsert
    suspend fun upsertAll(vararg location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()

    @Query("SELECT * FROM $TABLE_NAME WHERE name=:name ")
    fun getByName(name: String): Flow<Location>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<Location>>

    companion object {
        const val TABLE_NAME = "location"
    }
}