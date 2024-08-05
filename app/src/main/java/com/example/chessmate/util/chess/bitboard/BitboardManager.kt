package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.ChessBot
import com.example.chessmate.util.chess.GamePhase
import com.example.chessmate.util.chess.Player
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.chessboard.PieceColor
import com.example.chessmate.util.chess.chessboard.PieceType
import kotlinx.coroutines.*

class BitboardManager(private var listener: BitboardListener) {
    private val bitboard = Bitboard()
    private lateinit var moveGenerator: BitboardMoveGenerator
    private lateinit var evaluator: BitboardEvaluator
    private lateinit var player: Player
    private lateinit var bot: ChessBot
    private var availablePlayerMoves = mutableListOf<BitMove>()
    private var isMoveMadeByWhite = true
    private var trackedMoves: MutableList<BitMoveTracker> = mutableListOf()
    var turnNumber = 1

    fun initializeUIAndSquareListener(isPlayerStarted: Boolean) {
        initPlayerColors(isPlayerStarted)
        bitboard.setupInitialBoard()
        moveGenerator = BitboardMoveGenerator(bitboard)
        evaluator = BitboardEvaluator(bitboard)
        listener.setupInitialBoardUI(bitboard)
        listener.setupSquareListener(bitboard)
    }

    private fun initPlayerColors(isPlayerStarted: Boolean) {
        if (isPlayerStarted) {
            GameContext.playerColor = PieceColor.WHITE
            GameContext.botColor = PieceColor.BLACK
            GameContext.isPlayerTurn = true
            this.player = Player(PieceColor.WHITE)
            this.bot = ChessBot(PieceColor.BLACK)
        } else {
            GameContext.playerColor = PieceColor.WHITE
            GameContext.botColor = PieceColor.BLACK
            GameContext.isBotTurn = true
            this.player = Player(PieceColor.BLACK)
            this.bot = ChessBot(PieceColor.WHITE)
        }
    }

    fun startGame() {
        println("game started on bitboard")
        GameContext.gamePhase = GamePhase.OPENING
        if (GameContext.isBotTurn) {
            makeBotMove()
        }
    }

    fun switchTurns() {
        GameContext.isPlayerTurn = !GameContext.isPlayerTurn
        GameContext.isBotTurn = !GameContext.isBotTurn
        isMoveMadeByWhite = !isMoveMadeByWhite
        if (isMoveMadeByWhite) {
            turnNumber++
        }
        if (GameContext.isBotTurn) {
            makeBotMove()
        }
    }

    private fun makeBotMove() {
        CoroutineScope(Dispatchers.Main).launch {
            val move: BitMove? = withContext(Dispatchers.Default) {
                bot.findBestMove(bitboard, 5, GameContext.botColor == PieceColor.WHITE)
            }

            if (move != null) {
                bitboard.movePiece(move)
                trackedMoves.add(BitMoveTracker(move, turnNumber, GameContext.playerColor, GameContext.botColor, isMoveMadeByWhite))
                listener.onBotMoveMade(bitboard, move)
            } else {
                println("checkmate or stalemate")
                println("switching turns...")
                switchTurns()
            }
        }
    }

    fun processFirstClick(square: BitSquare) {
        availablePlayerMoves.clear()
        val legalMoves = moveGenerator.generateLegalMovesForPlayer()
        for (move in legalMoves) {
            val decodedMove = BitboardMoveGenerator.decodeMove(move)
            if (square.position == decodedMove.from) {
                availablePlayerMoves.add(decodedMove)
            }
        }
        listener.onPlayerMoveCalculated(availablePlayerMoves, square)
    }

    fun processSecondClick(square: BitSquare) {
        for (move in availablePlayerMoves) {
            if (move.to == square.position) {
                if (move.promotion != BitPiece.NONE) {
                    listener.showPromotionDialog(square)
                    break
                }
                bitboard.movePiece(move)
                trackedMoves.add(BitMoveTracker(move, turnNumber, GameContext.playerColor, GameContext.botColor, isMoveMadeByWhite))
                listener.onPlayerMoveMade(bitboard, move)
                break
            }
        }
    }

    fun processPawnPromotion(piece: PieceType, fromSquare: BitSquare, toSquare: BitSquare) {
        val bitPiece = convertPieceTypeToBitPiece(piece, fromSquare.color)
        println("bitPiece: $bitPiece")
        for (move in availablePlayerMoves) {
            if (move.to == toSquare.position && move.promotion == bitPiece) {
                bitboard.movePiece(move)
                trackedMoves.add(BitMoveTracker(move, turnNumber, GameContext.playerColor, GameContext.botColor, isMoveMadeByWhite))
                listener.onPlayerMoveMade(bitboard, move)
                break
            }
        }
    }

    private fun convertPieceTypeToBitPiece(piece: PieceType, color: PieceColor): BitPiece {
        println("piece: $piece, color: $color")
        return when (piece to color) {
            PieceType.QUEEN to PieceColor.WHITE -> BitPiece.WHITE_QUEEN
            PieceType.ROOK to PieceColor.WHITE -> BitPiece.WHITE_ROOK
            PieceType.BISHOP to PieceColor.WHITE -> BitPiece.WHITE_BISHOP
            PieceType.KNIGHT to PieceColor.WHITE -> BitPiece.WHITE_KNIGHT
            PieceType.QUEEN to PieceColor.BLACK -> BitPiece.BLACK_QUEEN
            PieceType.ROOK to PieceColor.BLACK -> BitPiece.BLACK_ROOK
            PieceType.BISHOP to PieceColor.BLACK -> BitPiece.BLACK_BISHOP
            PieceType.KNIGHT to PieceColor.BLACK -> BitPiece.BLACK_KNIGHT
            else -> BitPiece.NONE
        }
    }

    fun isKingInCheck(isForBot: Boolean): Boolean {
        if (!isForBot) {
            val kingPosition = if (GameContext.playerColor == PieceColor.WHITE) bitboard.getWhiteKing() else bitboard.getBlackKing()
            return moveGenerator.isSquareUnderAttack(kingPosition, isForBot, GameContext.playerColor)
        } else {
            val kingPosition = if (GameContext.botColor == PieceColor.WHITE) bitboard.getWhiteKing() else bitboard.getBlackKing()
            return moveGenerator.isSquareUnderAttack(kingPosition, isForBot, GameContext.botColor)
        }
    }

    fun getPlayerKingPosition(): Long {
        return if (GameContext.playerColor == PieceColor.WHITE) bitboard.getWhiteKing() else bitboard.getBlackKing()
    }

    fun getLastTrackedMove(): BitMoveTracker {
        return trackedMoves.last()
    }

    fun getLastTrackedWhiteMove(): BitMoveTracker {
        return trackedMoves.filter { it.isMoveMadeByWhite }.maxByOrNull { it.turnNumber } ?: throw NoSuchElementException("No move made by white found")
    }

    fun getLastTrackedBlackMove(): BitMoveTracker {
        return trackedMoves.filter { !it.isMoveMadeByWhite }.maxByOrNull { it.turnNumber } ?: throw NoSuchElementException("No move made by black found")
    }

    fun getBitPiece(position: Long): BitSquare {
        return bitboard.getPiece(position)
    }

    fun getSquareNotation(position: Long): String {
        return "${getFileName(position)}${getRankName(position)}"
    }

    fun getFileName(position: Long): String {
        val fileNames = "abcdefgh"
        val index = position.toULong().countTrailingZeroBits()
        val file = fileNames[index % 8]
        return "$file"
    }

    fun getRankName(position: Long): String {
        val rankNames = "12345678"
        val index = position.toULong().countTrailingZeroBits()
        val rank = rankNames[index / 8]
        return "$rank"
    }

    fun positionToRowCol(position: Long): Position {
        val bitIndex = 63 - java.lang.Long.numberOfLeadingZeros(position)
        val row = 7 - bitIndex / 8
        val col = bitIndex % 8
        return Position(row, col)
    }

    fun rowColToPosition(position: Position): Long {
        return if (GameContext.playerColor == PieceColor.WHITE) 1L shl ((7 - position.row) * 8 + position.col) else 1L shl (position.row * 8 + (7 - position.col))
    }
}