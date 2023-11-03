package com.example.chessmate.util.chess

data class MoveTracker(
    val sourceSquare: Square,
    val destinationSquare: Square,
    val turnNumber: Int,
    val isWhiteToMove: Boolean
)
