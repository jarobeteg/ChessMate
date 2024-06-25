package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.ChessBot
import com.example.chessmate.util.chess.Player
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.chessboard.PieceColor

class BitboardManager(private var listener: BitboardListener) {
    private val bitboard = Bitboard()
    private val bitboardMoveGenerator = BitboardMoveGenerator(bitboard)
    var isPlayerTurn = false
    private var availablePlayerMoves = mutableListOf<BitMove>()
    private lateinit var player: Player
    private lateinit var bot: ChessBot

    fun initializeUIAndSquareListener(isPlayerStarted: Boolean) {
        initPlayerColors(isPlayerStarted)
        bitboard.setupInitialBoard()
        listener.setupInitialBoardUI(bitboard)
        listener.setupSquareListener(bitboard)
    }

    private fun initPlayerColors(isPlayerStarted: Boolean) {
        if (isPlayerStarted) {
            this.player = Player(PieceColor.WHITE)
            this.bot = ChessBot(PieceColor.BLACK)
            isPlayerTurn = true
        } else {
            this.player = Player(PieceColor.BLACK)
            this.bot = ChessBot(PieceColor.WHITE)
            isPlayerTurn = false
        }
    }

    fun playerColor(): PieceColor {
        return player.color
    }

    fun botColor(): PieceColor {
        return bot.color
    }

    fun startGame() {
        println("game started on bitboard")
    }

    fun processFirstClick(square: BitSquare) {
        listener.onPlayerMoveCalculated(availablePlayerMoves, square)
    }

    fun processSecondClick(square: BitSquare) {}

    fun getBitPiece(position: Long): BitSquare {
        return bitboard.getPiece(position)
    }

    fun positionToRowCol(position: Long): Position {
        val bitIndex = 63 - java.lang.Long.numberOfLeadingZeros(position)
        val row = 7 - bitIndex / 8
        val col = bitIndex % 8
        return Position(row, col)
    }

    fun rowColToPosition(position: Position): Long {
        return if (playerColor() == PieceColor.WHITE) 1L shl ((7 - position.row) * 8 + position.col) else 1L shl (position.row * 8 + (7 - position.col))
    }
}