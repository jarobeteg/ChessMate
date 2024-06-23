package com.example.chessmate.util.chess

data class Square(
    val row: Int,
    val col: Int,
    var piece: Piece = Piece(PieceColor.NONE, PieceType.NONE)
) {
    fun copy(): Square {
        return Square(row, col, piece.copy())
    }

    fun clear() {
        this.piece.color = PieceColor.NONE
        this.piece.type = PieceType.NONE
    }
}