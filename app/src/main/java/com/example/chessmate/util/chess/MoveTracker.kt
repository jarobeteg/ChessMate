package com.example.chessmate.util.chess

data class MoveTracker(
    val sourceSquare: Square,
    val destinationSquare: Square,
    val pieceMoved: PieceType,
    val pieceTaken: PieceType?,
    val turnNumber: Int,
    val isWhiteToMove: Boolean
)
