package com.example.chessmate.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessoncompletions",
    foreignKeys = [
        ForeignKey(entity = UserProfile::class, parentColumns = ["userID"], childColumns = ["userID"])
    ]
)
data class LessonCompletion (
    @PrimaryKey(autoGenerate = true)
    val completionID: Int = 0,
    val userID: Long,
    val sectionID: Int,
    val lessonID: Int,
    val subLessonID: Int,
    val type: Int,
    val isSolved: Boolean
)