package com.example.chessmate.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userprofile")
data class UserProfile (
    @PrimaryKey(autoGenerate = true)
    val userID: Long = 0L,
    val username: String,
    var openingRating: Int,
    var midgameRating: Int,
    var endgameRating: Int,
    var level: Int,
    var gamesPlayed: Int,
    var puzzlesPlayed: Int,
    var lessonsTaken: Int,
    var isActive: Boolean
)