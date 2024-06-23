package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.Bitboard
import kotlin.system.measureTimeMillis

class ChessGameManager(private var listener: ChessGameListener) {
    private val chessboard = Chessboard()
    private val moveGenerator = LegalMoveGenerator()
    private val evaluator = ChessboardEvaluator()
    var isPlayerTurn = false
    private var availablePlayerMoves = mutableListOf<Move>()
    private lateinit var player: Player
    private lateinit var bot: ChessBot

    fun initializeUIAndSquareListener(isPlayerStarted: Boolean) {
        initPlayerColors(isPlayerStarted)
        chessboard.setupInitialBoard(isPlayerStarted)
        listener.setupInitialBoardUI(chessboard)
        listener.setupSquareListener(chessboard)
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
        println("game started")
    }

    fun movePiece(move: Move) {
        val fromSquare = chessboard.getSquare(move.from)
        val toSquare = chessboard.getSquare(move.to)

        toSquare.piece = fromSquare.piece.copy()
        fromSquare.clear()
        toSquare.piece.moved()
        listener.updateMoveHighlights(move.from, move.to)
    }

    fun processFirstClick(square: Square) {
        availablePlayerMoves.clear()
        val moves = mutableListOf<Move>()
        val pieceType = square.piece.type
        val position = Position(square.row, square.col)
        when (pieceType) {
            PieceType.PAWN -> moveGenerator.generateMovesForPawn(chessboard, position, playerColor(), -1, 6, 0, moves)
            PieceType.KNIGHT -> moveGenerator.generateMovesForKnight(chessboard, position, playerColor(), moves)
            PieceType.BISHOP -> moveGenerator.generateMovesForBishop(chessboard, position, playerColor(), moves)
            PieceType.ROOK -> moveGenerator.generateMovesForRook(chessboard, position, playerColor(), moves)
            PieceType.QUEEN -> moveGenerator.generateMovesForQueen(chessboard, position, playerColor(), moves)
            PieceType.KING -> moveGenerator.generateMovesForKing(chessboard, position, playerColor(), moves)
            else -> {}
        }

        availablePlayerMoves = moves.filter { move ->
            val newBoard = chessboard.simulateMove(move)
            !newBoard.isKingInCheck(playerColor(), false)
        }.toMutableList()
        listener.onPlayerMoveCalculated(availablePlayerMoves, square)
    }

    fun processSecondClick(square: Square) {
        val position = Position(square.row, square.col)
        for (move in availablePlayerMoves) {
            if (move.to == position) {
                movePiece(move)
            }
        }

        listener.onPlayerMoveMade(chessboard)
        availablePlayerMoves.clear()
    }

    private fun playerTurn() {}

    private fun botTurn() {}
}