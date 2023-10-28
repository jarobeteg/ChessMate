package com.example.chessmate.util.chess

class Chessboard {
    private val board: Array<Array<Square?>> = Array(8) { arrayOfNulls(8) }

    init {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                board[row][col] = Square(null, row, col)
            }
        }
    }

    fun placePiece(piece: ChessPiece, row: Int, col: Int) {
        board[row][col]?.piece = piece
    }

    fun getSquare(row: Int, col: Int): Square? {
        return board[row][col]
    }
}