package com.example.chessmate.util.chess

class Chessboard {
    private val board: Array<Array<Square>> = Array(8) { Array(8) { Square(false, null, null) } }

    init {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                board[row][col] = Square(false, null, null)
            }
        }
    }

    fun placePiece(row: Int, col: Int, pieceColor: PieceColor, pieceType: PieceType) {
        board[row][col] = Square(true, pieceColor, pieceType)
    }

    fun getSquare(row: Int, col: Int): Square {
        return board[row][col]
    }
}
