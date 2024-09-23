package com.example.chessmate.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "puzzlecompletions",
    foreignKeys = [
        ForeignKey(entity = UserProfile::class, parentColumns = ["userID"], childColumns = ["userID"]),
    ]
)
data class PuzzleCompletion(
    @PrimaryKey(autoGenerate = true)
    val completionID: Int = 0,
    val userID: Long,
    val puzzleID: Int,
    val difficulty: Int,
    val isSolved: Boolean
)
