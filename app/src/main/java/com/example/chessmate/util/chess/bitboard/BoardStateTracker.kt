package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.chessboard.PieceColor

data class BoardStateTracker(
    val bitboards: LongArray,
    val currentTurn: PieceColor
) {
    fun copy(): BoardStateTracker {
        return BoardStateTracker(bitboards.clone(), currentTurn)
    }
}