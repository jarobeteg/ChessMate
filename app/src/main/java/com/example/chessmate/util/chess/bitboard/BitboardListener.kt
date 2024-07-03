package com.example.chessmate.util.chess.bitboard

interface BitboardListener {
    fun setupInitialBoardUI(bitboard: Bitboard)
    fun setupSquareListener(bitboard: Bitboard)
    fun onPlayerMoveCalculated(moves: MutableList<BitMove>, square: BitSquare)
    fun onPlayerMoveMade(bitboard: Bitboard, move: BitMove)
}