package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.PieceColor

enum class BitPiece {
    WHITE_PAWN,
    WHITE_KNIGHT,
    WHITE_BISHOP,
    WHITE_ROOK,
    WHITE_QUEEN,
    WHITE_KING,
    BLACK_PAWN,
    BLACK_KNIGHT,
    BLACK_BISHOP,
    BLACK_ROOK,
    BLACK_QUEEN,
    BLACK_KING,
    NONE;

    companion object {
        fun fromOrdinal(ordinal: Int): BitPiece = entries.toTypedArray().getOrElse(ordinal) { NONE }
        fun toOrdinal(bitPiece: BitPiece): Int = bitPiece.ordinal
    }

    fun color(): PieceColor {
        return when (this) {
            WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING -> PieceColor.WHITE
            BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING -> PieceColor.BLACK
            NONE -> PieceColor.NONE
        }
    }
}