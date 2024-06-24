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

    fun getBitPiece(position: Long): BitSquare {
        val piece = bitboard.getPiece(position)
        return BitSquare(position, piece)
    }

    fun getSquareNotation(position: Long): String {
        val fileNames = "ABCDEFGH"
        val rankNames = "12345678"

        val index = position.toULong().countTrailingZeroBits().toInt()

        val file = fileNames[index % 8]
        val rank = rankNames[index / 8]

        return "$file$rank"
    }
}