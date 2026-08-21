package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history WHERE uid = :uid AND server = :server LIMIT 1")
    suspend fun findEntry(uid: String, server: String): SearchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchHistoryEntity): Long

    @Update
    suspend fun update(entity: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM search_history WHERE isFavorite = 0")
    suspend fun clearNonFavorites()

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}
