package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.PieceColor

data class BitSquare(val position: Long, val notation: String, val piece: BitPiece, val color: PieceColor, val hasMoved: Boolean = false)
