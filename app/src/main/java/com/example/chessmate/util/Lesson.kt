package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson (
    val lessonId: Int,
    val fen: String,
    val solution: String,
    val type: Int
) : Parcelable