package com.example.chessmate.util.chess

interface ChessGameListener {
    fun onMoveMade(move: Move)
    fun onPlayerMoveCalculated(legalMoves: MutableList<Move>, square: Square)
    fun kingIsInCheck(square: Square)
    fun checkForStalemate()
    fun checkForCheckmate()
}