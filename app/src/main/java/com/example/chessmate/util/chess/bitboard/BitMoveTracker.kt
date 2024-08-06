package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.PieceColor

data class BitMoveTracker(
    val bitMove: BitMove,
    var turnNumber: Int,
    val playerColor: PieceColor,
    val botColor: PieceColor,
    val isMoveMadeByWhite: Boolean
)
