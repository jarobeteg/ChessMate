package com.example.chessmate.util.chess

data class Square(
    val isOccupied: Boolean,
    val pieceColor: PieceColor?,
    val pieceType: PieceType?
)
