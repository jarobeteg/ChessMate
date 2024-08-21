package com.example.chessmate.util.chess.bitboard

data class BitMove(
    val from: Long,
    val to: Long,
    val piece: BitPiece,
    val capturedPiece: BitPiece = BitPiece.NONE,
    val promotion: BitPiece = BitPiece.NONE,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val isCheck: Boolean = false
)
