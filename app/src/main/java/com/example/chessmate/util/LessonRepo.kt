package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonRepo(
    val type: Int,
    val sectionId: Int,
    val lessonId: Int,
    val subLessonId: Int,
    val descriptionId: Int,
    val fen: String,
    val solution: String
) : Parcelable