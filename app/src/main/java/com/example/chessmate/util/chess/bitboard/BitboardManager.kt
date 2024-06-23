package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.ChessBot
import com.example.chessmate.util.chess.Move
import com.example.chessmate.util.chess.Player

class BitboardManager(private var listener: BitboardListener) {
    private val bitboard = Bitboard()
    private val bitboardMoveGenerator = BitboardMoveGenerator()
    var isPlayerTurn = true
    private var availablePlayerMoves = mutableListOf<Move>()
    private lateinit var player: Player
    private lateinit var bot: ChessBot

    fun initializeUIAndSquareListener() {
        bitboard.setupInitialBoard()
        listener.setupInitialBoardUI(bitboard)
        listener.setupSquareListener(bitboard)
    }

    fun startGame() {
        println("game started on bitboard")
    }

    fun getBitPiece(position: Long): BitPiece {
        return bitboard.getPiece(position)
    }
}