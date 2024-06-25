package com.example.chessmate.util.chess.bitboard

data class BitMove(
    val from: Long,
    val to: Long,
    val piece: BitPiece,
    val capturedPiece: BitPiece? = null,
    val promotion: BitPiece? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false
)
