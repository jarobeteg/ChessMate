package com.example.chessmate.util.chess.chessboard

data class Piece(
    var color: PieceColor,
    var type: PieceType,
    var hasMoved: Boolean = false
) {
    fun moved() {
        this.hasMoved = true
    }
}
