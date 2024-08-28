package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.PieceColor

class Bitboard {
    var bitboards = LongArray(12)
    var stateTracker = ArrayDeque<BoardStateTracker>()
    private var castlingRights: Int = 0xF
    private var fiftyMoveRule: Int = 0
    private var lastMove: Long = 0
    private var turnBit: Int = 0

    private val castlingRightsMap = mapOf(
        BitPiece.WHITE_KING to listOf(WHITE_KINGSIDE, WHITE_QUEENSIDE),
        BitPiece.BLACK_KING to listOf(BLACK_KINGSIDE, BLACK_QUEENSIDE),
        BitPiece.WHITE_ROOK to mapOf(BitCell.A1.bit to WHITE_QUEENSIDE, BitCell.H1.bit to WHITE_KINGSIDE),
        BitPiece.BLACK_ROOK to mapOf(BitCell.A8.bit to BLACK_QUEENSIDE, BitCell.H8.bit to BLACK_KINGSIDE)
    )

    companion object {
        const val RANK_1 = 0xFFL
        const val RANK_2 = RANK_1 shl 8
        const val RANK_3 = RANK_1 shl 16
        const val RANK_4 = RANK_1 shl 24
        const val RANK_5 = RANK_1 shl 32
        const val RANK_6 = RANK_1 shl 40
        const val RANK_7 = RANK_1 shl 48
        const val RANK_8 = RANK_1 shl 56

        const val FILE_A = 0x101010101010101L
        const val FILE_B = FILE_A shl 1
        const val FILE_C = FILE_A shl 2
        const val FILE_D = FILE_A shl 3
        const val FILE_E = FILE_A shl 4
        const val FILE_F = FILE_A shl 5
        const val FILE_G = FILE_A shl 6
        const val FILE_H = FILE_A shl 7

        const val WHITE_KINGSIDE = 0x1
        const val WHITE_QUEENSIDE = 0x2
        const val BLACK_KINGSIDE = 0x4
        const val BLACK_QUEENSIDE = 0x8
        const val WHITE_CASTLED = 0x10
        const val BLACK_CASTLED = 0x20
    }

    fun setupInitialBoard() {
        setPiece(BitPiece.WHITE_PAWN, RANK_2)
        setPiece(BitPiece.WHITE_ROOK, RANK_1 and (FILE_A or FILE_H))
        setPiece(BitPiece.WHITE_KNIGHT, RANK_1 and (FILE_B or FILE_G))
        setPiece(BitPiece.WHITE_BISHOP, RANK_1 and (FILE_C or FILE_F))
        setPiece(BitPiece.WHITE_QUEEN, RANK_1 and FILE_D)
        setPiece(BitPiece.WHITE_KING, RANK_1 and FILE_E)

        setPiece(BitPiece.BLACK_PAWN, RANK_7)
        setPiece(BitPiece.BLACK_ROOK, RANK_8 and (FILE_A or FILE_H))
        setPiece(BitPiece.BLACK_KNIGHT, RANK_8 and (FILE_B or FILE_G))
        setPiece(BitPiece.BLACK_BISHOP, RANK_8 and (FILE_C or FILE_F))
        setPiece(BitPiece.BLACK_QUEEN, RANK_8 and FILE_D)
        setPiece(BitPiece.BLACK_KING, RANK_8 and FILE_E)

        this.stateTracker.add(BoardStateTracker(bitboards.clone(), PieceColor.WHITE, 0L))
    }

    fun setupFENPosition(fen: FEN) {
        bitboards = LongArray(12) { 0L }

        val pieceMap = mapOf(
            'P' to BitPiece.WHITE_PAWN,
            'N' to BitPiece.WHITE_KNIGHT,
            'B' to BitPiece.WHITE_BISHOP,
            'R' to BitPiece.WHITE_ROOK,
            'Q' to BitPiece.WHITE_QUEEN,
            'K' to BitPiece.WHITE_KING,
            'p' to BitPiece.BLACK_PAWN,
            'n' to BitPiece.BLACK_KNIGHT,
            'b' to BitPiece.BLACK_BISHOP,
            'r' to BitPiece.BLACK_ROOK,
            'q' to BitPiece.BLACK_QUEEN,
            'k' to BitPiece.BLACK_KING
        )

        val rows = fen.piecePlacement.split("/")
        for (row in 0 until 8) {
            var col = 0
            for (char in rows[row]) {
                if (char.isDigit()) {
                    col += char.digitToInt()
                } else {
                    val piece = pieceMap[char] ?: continue
                    val square = 1L shl ((7 - row) * 8 + col)
                    setPiece(piece, square)
                    col++
                }
            }
        }

        val currentTurn = if (fen.activeColor == 'w') PieceColor.WHITE else PieceColor.BLACK

        castlingRights = 0
        if ('K' in fen.castlingRights) grantCastlingRights(WHITE_KINGSIDE)
        if ('Q' in fen.castlingRights) grantCastlingRights(WHITE_QUEENSIDE)
        if ('k' in fen.castlingRights) grantCastlingRights(BLACK_KINGSIDE)
        if ('q' in fen.castlingRights) grantCastlingRights(BLACK_QUEENSIDE)

        fiftyMoveRule = fen.halfMoveClock

        this.stateTracker.add(BoardStateTracker(bitboards.clone(), currentTurn, 0L))
    }

    fun updateBoardState(state: BoardStateTracker) {
        for (i in 0 until 12) {
            bitboards[i] = state.bitboards[i]
        }
    }

    fun getLastBoardState(): BoardStateTracker {
        return stateTracker.last()
    }

    fun getSecondLastBoardState(): BoardStateTracker {
        return stateTracker[stateTracker.size - 2]
    }

    private fun setPiece(piece: BitPiece, square: Long) {
        val index = piece.ordinal
        bitboards[index] = bitboards[index] or square
    }

    private fun removePiece(piece: BitPiece, square: Long) {
        if (piece != BitPiece.NONE) {
            val index = piece.ordinal
            bitboards[index] = bitboards[index] and square.inv()
        }
    }

    fun getPiece(square: Long): BitSquare {
        for ((index, bitboard) in bitboards.withIndex()) {
            if (bitboard and square != 0L) {
                val piece = BitPiece.entries[index]
                val color = if (index < 6) PieceColor.WHITE else PieceColor.BLACK
                return BitSquare(square, getSquareNotation(square), piece, color)
            }
        }
        return BitSquare(square, getSquareNotation(square), BitPiece.NONE, PieceColor.NONE)
    }

    //remove if it there is no need for this method
    fun undoMovePiece() {
        if (stateTracker.isEmpty()) return
        stateTracker.removeLast()
        updateBoardState(stateTracker.last())
    }

    fun movePiece(move: BitMove) {
        castlingRightsMap[move.piece].let { rights ->
            when (rights) {
                is List<*> -> rights.forEach { revokeCastlingRights(it as Int) }
                is Map<*, *> -> {
                    val right = rights[move.from] as? Int
                    if (right != null) {
                        revokeCastlingRights(right)
                    }
                }
            }
        }

        updateFiftyMoveRule(move)

        val encodedMove = BitboardMoveGenerator.encodeMove(move)
        lastMove = encodedMove

        if (move.capturedPiece == BitPiece.WHITE_ROOK || move.capturedPiece == BitPiece.BLACK_ROOK) revokeRookCastleRight(move)

        if (move.isCastling) {
            handleCastlingMove(move)
        } else if (move.promotion != BitPiece.NONE) {
            handlePromotionMove(move)
        } else if (move.isEnPassant) {
            handleEnPassantMove(move)
        } else {
            removePiece(move.capturedPiece, move.to)
            setPiece(move.piece, move.to)
            removePiece(move.piece, move.from)
        }

        this.stateTracker.add(BoardStateTracker(bitboards.clone(), move.piece.color(), encodedMove))
        toggleTurn()
    }

    private fun revokeRookCastleRight(move: BitMove) {
        if (move.capturedPiece == BitPiece.WHITE_ROOK) {
            if (move.to == BitCell.A1.bit) revokeCastlingRights(WHITE_QUEENSIDE)
            if (move.to == BitCell.H1.bit) revokeCastlingRights(WHITE_KINGSIDE)
        }
        if (move.capturedPiece == BitPiece.BLACK_ROOK) {
            if (move.to == BitCell.A8.bit) revokeCastlingRights(BLACK_QUEENSIDE)
            if (move.to == BitCell.H8.bit) revokeCastlingRights(BLACK_KINGSIDE)
        }
    }

    private fun handleCastlingMove(move: BitMove) {
        if (move.piece == BitPiece.WHITE_KING) {
            setWhiteCastled()
            if (move.to == BitCell.G1.bit) { //white kingside castles
                setPiece(BitPiece.WHITE_KING, BitCell.G1.bit)
                removePiece(BitPiece.WHITE_KING, BitCell.E1.bit)

                setPiece(BitPiece.WHITE_ROOK, BitCell.F1.bit)
                removePiece(BitPiece.WHITE_ROOK, BitCell.H1.bit)
            } else if (move.to == BitCell.C1.bit) { //white queenside castles
                setPiece(BitPiece.WHITE_KING, BitCell.C1.bit)
                removePiece(BitPiece.WHITE_KING, BitCell.E1.bit)

                setPiece(BitPiece.WHITE_ROOK, BitCell.D1.bit)
                removePiece(BitPiece.WHITE_ROOK, BitCell.A1.bit)
            }
        } else if (move.piece == BitPiece.BLACK_KING) {
            setBlackCastled()
            if (move.to == BitCell.G8.bit) { //black kingside castles
                setPiece(BitPiece.BLACK_KING, BitCell.G8.bit)
                removePiece(BitPiece.BLACK_KING, BitCell.E8.bit)

                setPiece(BitPiece.BLACK_ROOK, BitCell.F8.bit)
                removePiece(BitPiece.BLACK_ROOK, BitCell.H8.bit)
            } else if (move.to == BitCell.C8.bit) { //black queenside castles
                setPiece(BitPiece.BLACK_KING, BitCell.C8.bit)
                removePiece(BitPiece.BLACK_KING, BitCell.E8.bit)

                setPiece(BitPiece.BLACK_ROOK, BitCell.D8.bit)
                removePiece(BitPiece.BLACK_ROOK, BitCell.A8.bit)
            }
        }
    }

    private fun handlePromotionMove(move: BitMove) {
        removePiece(move.capturedPiece, move.to)
        setPiece(move.promotion, move.to)
        removePiece(move.piece, move.from)
    }

    private fun handleEnPassantMove(move: BitMove) {
        val capturedPawnPosition = if (move.to > move.from) move.to shr 8 else move.to shl 8
        removePiece(move.capturedPiece, capturedPawnPosition)
        setPiece(move.piece, move.to)
        removePiece(move.piece, move.from)
    }

    private fun revokeCastlingRights(rights: Int) {
        castlingRights = castlingRights and rights.inv()
    }

    fun grantCastlingRights(rights: Int) {
        castlingRights = castlingRights or rights
    }

    fun hasCastlingRights(rights: Int): Boolean {
        return (castlingRights and rights) != 0
    }

    private fun setWhiteCastled() {
        castlingRights = castlingRights or WHITE_CASTLED
    }

    private fun setBlackCastled() {
        castlingRights = castlingRights or BLACK_CASTLED
    }

    fun hasWhiteCastled(): Boolean {
        return (castlingRights and WHITE_CASTLED) != 0
    }

    fun hasBlackCastled(): Boolean {
        return (castlingRights and BLACK_CASTLED) != 0
    }

    fun isSquareEmpty(square: Long): Boolean {
        return (getAllPieces() and square) == 0L
    }

    fun endGameResult(): String {
        return when {
            isPlayerCheckmated() -> "player_checkmated"
            isBotCheckmated() -> "bot_checkmated"
            else -> "draw"
        }
    }


    fun isGameEnded(): Boolean {
        return isPlayerCheckmated() || isBotCheckmated() || isGameADraw()
    }

    fun isPlayerCheckmated(): Boolean {
        val moveGenerator = BitboardMoveGenerator(this)
        val playerMoves = moveGenerator.generateLegalMovesForPlayer()

        return playerMoves.isEmpty() && isPlayerInCheck()
    }

    fun isBotCheckmated(): Boolean {
        val moveGenerator = BitboardMoveGenerator(this)
        val botMoves = moveGenerator.generateLegalMovesForBot()

        return botMoves.isEmpty() && isBotInCheck()
    }

    private fun isGameADraw(): Boolean {
        return when {
            isThreefold() -> true
            isStalemate() -> true
            isInsufficientMaterial() -> true
            isFiftyMoveRule() -> true
            else -> false
        }
    }

    private fun isThreefold(): Boolean {
        val stateCounts = mutableMapOf<BoardStateTracker, Int>()

        for (state in stateTracker) {
            stateCounts[state] = stateCounts.getOrDefault(state, 0) + 1
        }

        return stateCounts.any { it.value >= 3 }
    }

    private fun isStalemate(): Boolean {
        val moveGenerator = BitboardMoveGenerator(this)
        if (isPlayerTurn()) {
            return moveGenerator.generateLegalMovesForPlayer().isEmpty() && !isPlayerInCheck()
        } else if (isBotTurn()) {
            return moveGenerator.generateLegalMovesForBot().isEmpty() && !isBotInCheck()
        }

        return false
    }

    private fun isInsufficientMaterial(): Boolean {
        val whitePieces = getWhitePieceBitboards()
        val blackPieces = getBlackPieceBitboards()

        val totalWhitePieces = whitePieces.sumOf { java.lang.Long.bitCount(it) }
        val totalBlackPieces = blackPieces.sumOf { java.lang.Long.bitCount(it) }

        //king vs king
        if (totalWhitePieces == 1 && totalBlackPieces == 1) return true

        //king vs king and knight
        if (totalWhitePieces == 1 && totalBlackPieces == 2 && containsOnly(blackPieces, BitPiece.BLACK_KING, BitPiece.BLACK_KNIGHT)) return true
        if (totalBlackPieces == 1 && totalWhitePieces == 2 && containsOnly(whitePieces, BitPiece.WHITE_KING, BitPiece.WHITE_KNIGHT)) return true

        //king vs king and bishop
        if (totalWhitePieces == 1 && totalBlackPieces == 2 && containsOnly(blackPieces, BitPiece.BLACK_KING, BitPiece.BLACK_BISHOP)) return true
        if (totalBlackPieces == 1 && totalWhitePieces == 2 && containsOnly(whitePieces, BitPiece.WHITE_KING, BitPiece.WHITE_BISHOP)) return true

        //king and bishop vs king and bishop of same color
        if (totalWhitePieces == 2 && totalBlackPieces == 2
            && containsOnly(whitePieces, BitPiece.WHITE_KING, BitPiece.WHITE_BISHOP)
            && containsOnly(blackPieces, BitPiece.BLACK_KING, BitPiece.BLACK_BISHOP)
            && areBishopsOnSameColor()) return true

        return false
    }

    private fun containsOnly(pieceBitboards: LongArray, vararg pieces: BitPiece): Boolean {
        val pieceSet = pieces.map { it.ordinal }.toSet()
        val relevantIndices = pieces.map { it.ordinal }
        return relevantIndices.all { idx -> pieceBitboards[idx % 6] == 0L || pieceSet.contains(idx) }
    }

    private fun areBishopsOnSameColor(): Boolean {
        val whiteBishopPositions = bitboards[BitPiece.WHITE_BISHOP.ordinal]
        val blackBishopPositions = bitboards[BitPiece.BLACK_BISHOP.ordinal]

        val whiteBishopsOnLightSquares = countBishopsOnColorSquares(whiteBishopPositions, false)
        val whiteBishopsOnDarkSquares = countBishopsOnColorSquares(whiteBishopPositions, true)
        val blackBishopsOnLightSquares = countBishopsOnColorSquares(blackBishopPositions, false)
        val blackBishopsOnDarkSquares = countBishopsOnColorSquares(blackBishopPositions, true)

        val allBishopsOnLightSquares = (whiteBishopsOnLightSquares > 0 && blackBishopsOnLightSquares > 0)
        val allBishopsOnDarkSquares = (whiteBishopsOnDarkSquares > 0 && blackBishopsOnDarkSquares > 0)

        return allBishopsOnLightSquares || allBishopsOnDarkSquares
    }

    private fun countBishopsOnColorSquares(bishopPositions: Long, isDarkSquare: Boolean): Int {
        var count = 0
        var positions = bishopPositions

        while (positions != 0L) {
            val position = positions.takeLowestOneBit()
            val index = position.countTrailingZeroBits()
            val rank = index / 8
            val file = index % 8
            val isDarkSquarePosition = (rank + file) % 2 == 0
            if (isDarkSquarePosition == isDarkSquare) {
                count++
            }
            positions = positions xor position
        }

        return count
    }


    private fun isFiftyMoveRule(): Boolean {
        return fiftyMoveRule >= 100
    }

    private fun updateFiftyMoveRule(move: BitMove) {
        if (move.piece != BitPiece.WHITE_PAWN && move.piece != BitPiece.BLACK_PAWN && move.capturedPiece == BitPiece.NONE) {
            fiftyMoveRule ++
        } else {
            fiftyMoveRule = 0
        }
    }

    fun isPlayerInCheck(): Boolean {
        val moveGenerator = BitboardMoveGenerator(this)
        val kingPosition = if (GameContext.playerColor == PieceColor.WHITE) getWhiteKing() else getBlackKing()

        return moveGenerator.isSquareUnderAttack(kingPosition, false, GameContext.playerColor)
    }

    fun isBotInCheck(): Boolean {
        val moveGenerator = BitboardMoveGenerator(this)
        val kingPosition = if (GameContext.botColor == PieceColor.WHITE) getWhiteKing() else getBlackKing()

        return moveGenerator.isSquareUnderAttack(kingPosition, true, GameContext.botColor)
    }

    private fun getSquareNotation(position: Long): String {
        val fileNames = "ABCDEFGH"
        val rankNames = "12345678"

        val index = position.toULong().countTrailingZeroBits()

        val file = fileNames[index % 8]
        val rank = rankNames[index / 8]

        return "$file$rank"
    }

    fun isPlayerTurn(): Boolean {
        return turnBit and 1 == 1
    }

    fun isBotTurn(): Boolean {
        return turnBit and 1 == 0
    }

    private fun toggleTurn() {
        turnBit = turnBit xor 1
    }

   fun grantPlayerTurn() {
        turnBit = turnBit or 1
    }

    fun grantBotTurn() {
        turnBit = turnBit and 1.inv()
    }

    fun revokePlayerTurn() {
        turnBit = turnBit and 1.inv()
    }

    fun revokeBotTurn() {
        turnBit = turnBit or 1
    }

    fun setTurn(isPlayerTurn: Boolean) {
        turnBit = if (isPlayerTurn) 1 else 0
    }

    fun getWhitePieceBitboards(): LongArray {
        return longArrayOf(
            bitboards[BitPiece.WHITE_PAWN.ordinal],
            bitboards[BitPiece.WHITE_KNIGHT.ordinal],
            bitboards[BitPiece.WHITE_BISHOP.ordinal],
            bitboards[BitPiece.WHITE_ROOK.ordinal],
            bitboards[BitPiece.WHITE_QUEEN.ordinal],
            bitboards[BitPiece.WHITE_KING.ordinal]
        )
    }

    fun getBlackPieceBitboards(): LongArray {
        return longArrayOf(
            bitboards[BitPiece.BLACK_PAWN.ordinal],
            bitboards[BitPiece.BLACK_KNIGHT.ordinal],
            bitboards[BitPiece.BLACK_BISHOP.ordinal],
            bitboards[BitPiece.BLACK_ROOK.ordinal],
            bitboards[BitPiece.BLACK_QUEEN.ordinal],
            bitboards[BitPiece.BLACK_KING.ordinal]
        )
    }

    fun getAllWhitePieces(): Long {
        return bitboards[BitPiece.WHITE_PAWN.ordinal] or
                bitboards[BitPiece.WHITE_KNIGHT.ordinal] or
                bitboards[BitPiece.WHITE_BISHOP.ordinal] or
                bitboards[BitPiece.WHITE_ROOK.ordinal] or
                bitboards[BitPiece.WHITE_QUEEN.ordinal] or
                bitboards[BitPiece.WHITE_KING.ordinal]
    }

    fun getAllBlackPieces(): Long {
        return bitboards[BitPiece.BLACK_PAWN.ordinal] or
                bitboards[BitPiece.BLACK_KNIGHT.ordinal] or
                bitboards[BitPiece.BLACK_BISHOP.ordinal] or
                bitboards[BitPiece.BLACK_ROOK.ordinal] or
                bitboards[BitPiece.BLACK_QUEEN.ordinal] or
                bitboards[BitPiece.BLACK_KING.ordinal]
    }

    fun getAllPieces(): Long {
        return getAllWhitePieces() or getAllBlackPieces()
    }

    fun getKing(color: PieceColor): Long {
        return if (color == PieceColor.WHITE) getWhiteKing() else getBlackKing()
    }

    fun getWhiteKing(): Long {
        return bitboards[BitPiece.WHITE_KING.ordinal]
    }

    fun getBlackKing(): Long {
        return bitboards[BitPiece.BLACK_KING.ordinal]
    }

    fun getLastMove(): Long {
        return lastMove
    }

    fun getBitPiece(position: Long): BitPiece {
        for ((index, bitboard) in bitboards.withIndex()) {
            if ((bitboard and position) != 0L) {
                return BitPiece.entries[index]
            }
        }
        return BitPiece.NONE
    }

    fun restore(bitboard: Bitboard) {
        this.bitboards = bitboard.bitboards.clone()
        this.stateTracker = ArrayDeque(bitboard.stateTracker.map { it.copy() })
        this.castlingRights = bitboard.castlingRights
        this.fiftyMoveRule = bitboard.fiftyMoveRule
        this.lastMove = bitboard.lastMove
        this.turnBit = bitboard.turnBit
    }

    fun copy(): Bitboard {
        val copy = Bitboard()
        copy.bitboards = this.bitboards.clone()
        copy.stateTracker = ArrayDeque(this.stateTracker.map { it.copy() })
        copy.castlingRights = this.castlingRights
        copy.fiftyMoveRule = this.fiftyMoveRule
        copy.lastMove = this.lastMove
        copy.turnBit = this.turnBit
        return copy
    }

    override fun toString(): String {
        val whitePieces = getAllWhitePieces()
        val blackPieces = getAllBlackPieces()

        val bitboardsString = bitboards.joinToString(separator = "|") { bitboard ->
            bitboard.toString(2).padStart(64, '0')
        }

        return "W:$whitePieces|B:$blackPieces|$bitboardsString"
    }
}