package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChessBasics (
    val lessonId: Int,
    val descriptionId: Int,
    val fen: String,
    val solution: String,
    val type: Int
) : Parcelable