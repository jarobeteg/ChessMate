package com.example.chessmate.util.chess

data class MoveTracker(
    val move: Move,
    var turnNumber: Int,
    val playerColor: PieceColor,
    val botColor: PieceColor,
    val isWhiteToMove: Boolean
)
