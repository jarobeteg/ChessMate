package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.ChessBot
import com.example.chessmate.util.chess.FENHolder
import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.GamePhase
import com.example.chessmate.util.chess.Player
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.PieceType
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
    private var currentIndex = -1
    var isNavigating = false
    var turnNumber = 1
    private var queuedBotMove: BitMove? = null
    var isBotCalculating = false
    var wasBotCalculatingBeforePause = false
    var botCalculation: Job? = null

    companion object {
        fun positionToRowCol(position: Long): Position {
            val bitIndex = 63 - java.lang.Long.numberOfLeadingZeros(position)
            val row = 7 - bitIndex / 8
            val col = bitIndex % 8
            return Position(row, col)
        }
    }

    fun initializeUIAndSquareListener(isPlayerStarted: Boolean) {
        val fen = FENHolder.getFEN()
        if (fen != null) {
            initPlayerColorsFEN(isPlayerStarted)
            bitboard.setupFENPosition(fen)
            isMoveMadeByWhite = fen.activeColor == 'w'
            turnNumber = fen.fullMove
        } else {
            initPlayerColors(isPlayerStarted)
            bitboard.setupInitialBoard()
        }
        moveGenerator = BitboardMoveGenerator(bitboard)
        evaluator = BitboardEvaluator(bitboard)
        listener.setupInitialBoardUI(bitboard)
        listener.setupSquareListener(bitboard)
    }

    private fun initPlayerColorsFEN(isPlayerStarted: Boolean) {
        GameContext.gamePhase = GamePhase.OPENING
        updateGamePhase()
        if (isPlayerStarted) {
            GameContext.playerColor = PieceColor.WHITE
            GameContext.botColor = PieceColor.BLACK
            this.player = Player(PieceColor.WHITE)
            this.bot = ChessBot(PieceColor.BLACK)
            bitboard.setTurn(true)
        } else {
            GameContext.playerColor = PieceColor.BLACK
            GameContext.botColor = PieceColor.WHITE
            this.player = Player(PieceColor.BLACK)
            this.bot = ChessBot(PieceColor.WHITE)
            bitboard.setTurn(false)
        }
    }

    private fun initPlayerColors(isPlayerStarted: Boolean) {
        GameContext.gamePhase = GamePhase.OPENING
        if (isPlayerStarted) {
            GameContext.playerColor = PieceColor.WHITE
            GameContext.botColor = PieceColor.BLACK
            GameContext.isPlayerTurn = true
            GameContext.isBotTurn = false
            this.player = Player(PieceColor.WHITE)
            this.bot = ChessBot(PieceColor.BLACK)
            bitboard.setTurn(true)
        } else {
            GameContext.playerColor = PieceColor.BLACK
            GameContext.botColor = PieceColor.WHITE
            GameContext.isPlayerTurn = false
            GameContext.isBotTurn = true
            this.player = Player(PieceColor.BLACK)
            this.bot = ChessBot(PieceColor.WHITE)
            bitboard.setTurn(false)
        }
    }

    fun startGame() {
        listener.updateTurnTitle()
        if (GameContext.isBotTurn) {
            makeBotMove()
        }
    }

    fun resign() {
        GameContext.isPlayerTurn = false
        GameContext.isBotTurn = false
        listener.showEndGameDialog("player_checkmated")
    }

    private fun endGame() {
        GameContext.isPlayerTurn = false
        GameContext.isBotTurn = false
        listener.showEndGameDialog(bitboard.endGameResult())
    }

    fun switchTurns() {
        updateGamePhase()
        GameContext.isPlayerTurn = !GameContext.isPlayerTurn
        GameContext.isBotTurn = !GameContext.isBotTurn
        currentIndex = bitboard.stateTracker.size - 1

        if (bitboard.isGameEnded()) {
            endGame()
            return
        }
        listener.updateTurnTitle()

        isMoveMadeByWhite = !isMoveMadeByWhite
        if (isMoveMadeByWhite) {
            turnNumber++
        }
        if (GameContext.isBotTurn) {
            makeBotMove()
        }
    }

    private fun makeBotMove() {
        botCalculation = CoroutineScope(Dispatchers.Main).launch {
            isBotCalculating = true
            val move: BitMove? = withContext(Dispatchers.Default) {
                bot.findBestMove(bitboard, GameContext.depth, GameContext.botColor == PieceColor.WHITE)
            }

            if (move != null) {
                if (isNavigating) {
                    queuedBotMove = move
                } else {
                    executeBotMove(move)
                }
            }
            isBotCalculating = false
        }
    }

    private fun executeBotMove(move: BitMove) {
        bitboard.movePiece(move)
        trackedMoves.add(BitMoveTracker(move, turnNumber, GameContext.playerColor, GameContext.botColor, isMoveMadeByWhite))
        listener.onBotMoveMade(bitboard, move)
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
        for (move in availablePlayerMoves) {
            if (move.to == toSquare.position && move.promotion == bitPiece) {
                bitboard.movePiece(move)
                trackedMoves.add(BitMoveTracker(move, turnNumber, GameContext.playerColor, GameContext.botColor, isMoveMadeByWhite))
                listener.onPlayerMoveMade(bitboard, move)
                break
            }
        }
    }

    private fun updateGamePhase() {
        val currentPhase = GameContext.gamePhase
        when (currentPhase) {
            GamePhase.OPENING -> if (isMidGame()) GameContext.gamePhase = GamePhase.MIDGAME
            GamePhase.MIDGAME -> if (isEndGame()) GameContext.gamePhase = GamePhase.ENDGAME
            else -> {}
        }
    }

    private fun isMidGame(): Boolean {
        val developedMinorPieces = countDevelopedMinorPieces() >= 4
        val kingsCastled = isKingCastled(PieceColor.WHITE) || isKingCastled(PieceColor.BLACK)
        val centralPawnsMoved = centralPawnsMoved()

        return developedMinorPieces && kingsCastled && centralPawnsMoved
    }

    private fun countDevelopedMinorPieces(): Int {
        val whiteInitialPositions = arrayOf(
            Pair(BitCell.B1.bit, BitPiece.WHITE_KNIGHT),
            Pair(BitCell.G1.bit, BitPiece.WHITE_KNIGHT),
            Pair(BitCell.C1.bit, BitPiece.WHITE_BISHOP),
            Pair(BitCell.F1.bit, BitPiece.WHITE_BISHOP)
        )
        val blackInitialPositions = arrayOf(
            Pair(BitCell.B8.bit, BitPiece.BLACK_KNIGHT),
            Pair(BitCell.G8.bit, BitPiece.BLACK_KNIGHT),
            Pair(BitCell.C8.bit, BitPiece.BLACK_BISHOP),
            Pair(BitCell.F8.bit, BitPiece.BLACK_BISHOP)
        )

        val initialPositions = whiteInitialPositions + blackInitialPositions

        return initialPositions.count { (pos, piece) ->
            val currentPiece = bitboard.getBitPiece(pos)
            currentPiece != piece
        }
    }

    private fun isKingCastled(color: PieceColor): Boolean {
        val (hasKingCastled, kingside, queenside) = if (color == PieceColor.WHITE) {
            Triple(bitboard.hasWhiteCastled(), Bitboard.WHITE_KINGSIDE, Bitboard.WHITE_QUEENSIDE)
        } else {
            Triple(bitboard.hasBlackCastled(), Bitboard.BLACK_KINGSIDE, Bitboard.BLACK_QUEENSIDE)
        }

        val hasCastlingRights = bitboard.hasCastlingRights(kingside) || bitboard.hasCastlingRights(queenside)

        return hasKingCastled || !hasCastlingRights
    }

    private fun centralPawnsMoved(): Boolean {
        val initialPositions = arrayOf(
            Pair(BitCell.D2.bit, BitPiece.WHITE_PAWN),
            Pair(BitCell.E2.bit, BitPiece.WHITE_PAWN),
            Pair(BitCell.D7.bit, BitPiece.BLACK_PAWN),
            Pair(BitCell.E7.bit, BitPiece.BLACK_PAWN)
        )

        return initialPositions.count { (pos, piece) ->
            val currentPiece = bitboard.getBitPiece(pos)
            currentPiece != piece
        } >= 2
    }

    private fun isEndGame(): Boolean {
        val totalPieces = countTotalPieces() <= 6
        val fewPawns = countPawns()
        val fewRooksAndQueens = countRooksAndQueens() <= 4

        return totalPieces && fewPawns && fewRooksAndQueens
    }

    fun boardStateBackwards() {
        if (currentIndex > 0) {
            currentIndex--
            isNavigating = true
            displayBoardState(bitboard.stateTracker[currentIndex])
        }
    }

    fun boardStateForwards() {
        if (currentIndex == -1 && bitboard.stateTracker.size == 1) return
        if (currentIndex < bitboard.stateTracker.size - 1) {
            currentIndex++
            isNavigating = true
            displayBoardState(bitboard.stateTracker[currentIndex])
        }
    }

    fun boardStateContinue() {
        if (currentIndex == -1 && bitboard.stateTracker.size == 1) return
        currentIndex = bitboard.stateTracker.size - 1
        displayBoardState(bitboard.stateTracker[bitboard.stateTracker.size - 1])
    }

    private fun displayBoardState(state: BoardStateTracker) {
        bitboard.updateBoardState(state)
        if (currentIndex == bitboard.stateTracker.size - 1) {
            isNavigating = false
            val lastMove = bitboard.getLastMove()
            val decodedMove = BitboardMoveGenerator.decodeMove(lastMove)
            listener.showPreviousBoardState(bitboard, decodedMove)

            queuedBotMove?.let {
                executeBotMove(it)
                queuedBotMove = null
            }
        } else {
            listener.showPreviousBoardState(bitboard, null)
        }
    }

    private fun countTotalPieces(): Int {
        val whitePieceBoards = bitboard.getWhitePieceBitboards()
        val blackPieceBoards = bitboard.getBlackPieceBitboards()
        val whiteKnight = whitePieceBoards[1]
        val whiteBishop = whitePieceBoards[2]
        val whiteRook = whitePieceBoards[3]
        val whiteQueen = whitePieceBoards[4]
        val blackKnight = blackPieceBoards[1]
        val blackBishop = blackPieceBoards[2]
        val blackRook = blackPieceBoards[3]
        val blackQueen = blackPieceBoards[4]

        val whiteKnightCount = countPieces(whiteKnight)
        val whiteBishopCount = countPieces(whiteBishop)
        val whiteRookCount = countPieces(whiteRook)
        val whiteQueenCount = countPieces(whiteQueen)
        val blackKnightCount = countPieces(blackKnight)
        val blackBishopCount = countPieces(blackBishop)
        val blackRookCount = countPieces(blackRook)
        val blackQueenCount = countPieces(blackQueen)

        val result = whiteKnightCount + whiteBishopCount + whiteRookCount + whiteQueenCount +
                blackKnightCount + blackBishopCount + blackRookCount + blackQueenCount

        return result
    }

    private fun countPawns(): Boolean {
        val whitePawns = bitboard.getWhitePieceBitboards()[0]
        val blackPawns = bitboard.getBlackPieceBitboards()[0]

        val whitePawnCount = java.lang.Long.bitCount(whitePawns)
        val blackPawnCount = java.lang.Long.bitCount(blackPawns)

        return whitePawnCount <= 6 && blackPawnCount <= 6
    }

    private fun countRooksAndQueens(): Int {
        val whitePieceBoards = bitboard.getWhitePieceBitboards()
        val blackPieceBoards = bitboard.getBlackPieceBitboards()
        val whiteRook = whitePieceBoards[3]
        val whiteQueen = whitePieceBoards[4]
        val blackRook = blackPieceBoards[3]
        val blackQueen = blackPieceBoards[4]

        val whiteRookCount = countPieces(whiteRook)
        val whiteQueenCount = countPieces(whiteQueen)
        val blackRookCount = countPieces(blackRook)
        val blackQueenCount = countPieces(blackQueen)

        return whiteRookCount + whiteQueenCount + blackRookCount + blackQueenCount
    }

    private fun countPieces(pieces: Long): Int {
        return java.lang.Long.bitCount(pieces)
    }

    private fun convertPieceTypeToBitPiece(piece: PieceType, color: PieceColor): BitPiece {
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
        return if (isForBot) bitboard.isBotInCheck() else bitboard.isPlayerInCheck()
    }

    fun isKingCheckMated(isForBot: Boolean): Boolean {
        return if (isForBot) bitboard.isBotCheckmated() else bitboard.isPlayerCheckmated()
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

    fun getBitSquare(position: Long): BitSquare {
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
}