package com.example.chessmate.util.chess

abstract class ChessPiece(val color: ChessColor, var row: Int, var col: Int){
    abstract fun isValidMove(board: Chessboard, newRow: Int, newCol: Int): Boolean
}