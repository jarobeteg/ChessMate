package com.example.chessmate.util.chess.bitboard

interface BitboardListener {
    fun setupInitialBoardUI(bitboard: Bitboard)
    fun setupSquareListener(bitboard: Bitboard)
    fun showPromotionDialog(square: BitSquare)
    fun onPlayerMoveCalculated(moves: MutableList<BitMove>, square: BitSquare)
    fun onPlayerMoveMade(bitboard: Bitboard, move: BitMove)
    fun onBotMoveMade(bitboard: Bitboard, move: BitMove)
    fun showEndGameDialog(endGameResult: String)
}