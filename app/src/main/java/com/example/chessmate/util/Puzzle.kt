package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Puzzle(
    val puzzleId: Int,
    val fen: String,
    val solution: String,
    val difficulty: Int
) : Parcelable
