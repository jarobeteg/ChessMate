package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.PieceColor

data class BoardStateTracker(
    val bitboards: LongArray,
    val currentTurn: PieceColor,
    val move: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoardStateTracker) return false

        if (!bitboards.contentEquals(other.bitboards)) return false
        if (currentTurn != other.currentTurn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitboards.contentHashCode()

        result = 31 * result + currentTurn.hashCode()

        return result
    }

    fun copy(): BoardStateTracker {
        return BoardStateTracker(bitboards.clone(), currentTurn, move)
    }
}