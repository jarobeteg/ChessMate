package com.example.chessmate.util.chess

interface ChessGameListener {
    fun setupInitialBoardUI(chessboard: Chessboard)
    fun setupSquareListener(chessboard: Chessboard)
    fun onMoveMade(chessboard: Chessboard)
    fun onPlayerMoveCalculated(moves: MutableList<Move>, square: Square)
    fun onPlayerMoveMade(chessboard: Chessboard)
    fun updateMoveHighlights(from: Position, to: Position)
}