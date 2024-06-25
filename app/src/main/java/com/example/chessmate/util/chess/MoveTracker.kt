package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.chessboard.Move
import com.example.chessmate.util.chess.chessboard.PieceColor

data class MoveTracker(
    val move: Move,
    var turnNumber: Int,
    val playerColor: PieceColor,
    val botColor: PieceColor,
    val isWhiteToMove: Boolean
)
