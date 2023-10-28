package com.example.chessmate.util.chess

class Square(var piece: ChessPiece?, val row: Int, val col: Int) {

    fun isOccupied(): Boolean {
        return piece != null
    }

    fun clearSquare() {
        piece = null
    }

    fun getColor(): ChessColor? {
        return piece?.color
    }

    fun <T : ChessPiece> getPiece(pieceClass: Class<T>): T? {
        return pieceClass.cast(piece)
    }
}