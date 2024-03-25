package com.example.chessmate.util.chess

data class MoveTracker(
    val move: Move,
    val turnNumber: Int,
    val isWhiteToMove: Boolean
)
