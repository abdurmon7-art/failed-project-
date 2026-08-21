package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val uid: String,
    val server: String,
    val playerName: String,
    val playerLevel: Int,
    val playerLikes: Long,
    val brRankPoints: Int = 0,
    val csRankPoints: Int = 0,
    val guildName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
