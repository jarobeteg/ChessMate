package com.example.chessmate.util.chess

data class Piece(
    var color: PieceColor,
    var type: PieceType,
    var hasMoved: Boolean = false
) {
    fun moved() {
        this.hasMoved = true
    }
}
