package com.example.chessmate.util

data class Puzzle(
    val puzzleId: Int,
    val fen: String,
    val solution: String,
    val difficulty: Int
)
