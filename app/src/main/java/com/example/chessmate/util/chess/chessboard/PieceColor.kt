package com.example.chessmate.util.chess.chessboard

enum class PieceColor {
    WHITE,
    BLACK,
    NONE;

    fun opposite(): PieceColor {
        return when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
            NONE -> NONE
        }
    }
}