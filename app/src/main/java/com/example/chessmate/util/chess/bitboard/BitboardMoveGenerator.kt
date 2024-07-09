package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.chessboard.PieceColor
import kotlin.math.abs

class BitboardMoveGenerator (private val bitboard: Bitboard, private val playerColor: PieceColor, private val botColor: PieceColor) {

    companion object {
        fun encodeMove(
            from: Int,
            to: Int,
            piece: Int,
            capturedPiece: Int = 12,
            promotion: Int = 12,
            isCastling: Boolean = false,
            isEnPassant: Boolean = false
        ): Long {
            var encodedMove: Long = 0
            encodedMove = encodedMove or (from.toLong() and 0x3FL)
            encodedMove = encodedMove or ((to.toLong() and 0x3FL) shl 6)
            encodedMove = encodedMove or ((piece.toLong() and 0xFL) shl 12)
            encodedMove = encodedMove or ((capturedPiece.toLong() and 0xFL) shl 16)
            encodedMove = encodedMove or ((promotion.toLong() and 0xFL) shl 20)
            if (isCastling) encodedMove = encodedMove or (1L shl 24)
            if (isEnPassant) encodedMove = encodedMove or (1L shl 25)
            return encodedMove
        }

        fun decodeMove(encodedMove: Long): BitMove {
            val from = (encodedMove and 0x3FL).toInt()
            val to = ((encodedMove shr 6) and 0x3FL).toInt()
            val piece = ((encodedMove shr 12) and 0xFL).toInt()
            val capturedPiece = ((encodedMove shr 16) and 0xFL).toInt()
            val promotion = ((encodedMove shr 20) and 0xFL).toInt()
            val isCastling = (encodedMove shr 24) and 1L == 1L
            val isEnPassant = (encodedMove shr 25) and 1L == 1L

            return BitMove(
                from = 1L shl from,
                to = 1L shl to,
                piece = BitPiece.fromOrdinal(piece),
                capturedPiece = if (capturedPiece != 12) BitPiece.fromOrdinal(capturedPiece) else BitPiece.NONE,
                promotion = if (promotion != 12) BitPiece.fromOrdinal(promotion) else BitPiece.NONE,
                isCastling = isCastling,
                isEnPassant = isEnPassant
            )
        }
    }

    private val knightOffsets = intArrayOf(15, 6, 10, 17, -17, -10, -6, -15)
    private val bishopOffsets = intArrayOf(9, -9, 7, -7)
    private val rookOffsets = intArrayOf(1, -1, 8, -8)
    private val queenOffsets = intArrayOf(1, -1, 7, -7, 8, -8, 9, -9)
    private val kingOffsets = intArrayOf(1, -1, 7, -7, 8, -8, 9, -9)

    private var playerPiecesArray: LongArray
    private var playerPieces: Long
    private var botPiecesArray: LongArray
    private var botPieces: Long
    private var allPieces: Long
    private var emptySquares: Long

    init {
        allPieces = bitboard.getAllPieces()
        emptySquares = allPieces.inv()

        if (playerColor == PieceColor.WHITE) {
            playerPiecesArray = bitboard.getWhitePieceBitboards()
            playerPieces = bitboard.getAllWhitePieces()
            botPiecesArray = bitboard.getBlackPieceBitboards()
            botPieces = bitboard.getAllBlackPieces()
        } else {
            playerPiecesArray = bitboard.getBlackPieceBitboards()
            playerPieces = bitboard.getAllBlackPieces()
            botPiecesArray = bitboard.getWhitePieceBitboards()
            botPieces = bitboard.getAllWhitePieces()
        }
    }

    fun updateBoards() {
        allPieces = bitboard.getAllPieces()
        emptySquares = allPieces.inv()

        if (playerColor == PieceColor.WHITE) {
            playerPiecesArray = bitboard.getWhitePieceBitboards()
            playerPieces = bitboard.getAllWhitePieces()
            botPiecesArray = bitboard.getBlackPieceBitboards()
            botPieces = bitboard.getAllBlackPieces()
        } else {
            playerPiecesArray = bitboard.getBlackPieceBitboards()
            playerPieces = bitboard.getAllBlackPieces()
            botPiecesArray = bitboard.getWhitePieceBitboards()
            botPieces = bitboard.getAllWhitePieces()
        }
    }

    fun generateAllMoves(): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()

        moves.addAll(generateMovesForBot())
        moves.addAll(generateMovesForPlayer())

        return moves
    }

    fun generateAllLegalMoves(): ArrayDeque<Long>  {
        val moves = ArrayDeque<Long>()

        moves.addAll(generateLegalMovesForBot())
        moves.addAll(generateLegalMovesForPlayer())

        return moves
    }

    fun generateMovesForBot(): ArrayDeque<Long> {
        updateBoards()
        val moves = ArrayDeque<Long>()

        moves.addAll(generatePawnMoves(botPiecesArray[0], playerPieces, emptySquares, botColor))
        moves.addAll(generateKnightMoves(botPiecesArray[1], playerPieces, emptySquares))
        moves.addAll(generateBishopMoves(botPiecesArray[2], playerPieces, allPieces))
        moves.addAll(generateRookMoves(botPiecesArray[3], playerPieces, allPieces))
        moves.addAll(generateQueenMoves(botPiecesArray[4], playerPieces, allPieces))
        moves.addAll(generateKingMoves(botPiecesArray[5], playerPieces, botPieces, true))

        return moves
    }

    fun generateLegalMovesForBot(): ArrayDeque<Long> {
        return filterOutIllegalMoves(generateMovesForBot(), botPiecesArray[5], true, botColor)
    }

    fun generateMovesForPlayer(): ArrayDeque<Long> {
        updateBoards()
        val moves = ArrayDeque<Long>()

        moves.addAll(generatePawnMoves(playerPiecesArray[0], botPieces, emptySquares, playerColor))
        moves.addAll(generateKnightMoves(playerPiecesArray[1], botPieces, emptySquares))
        moves.addAll(generateBishopMoves(playerPiecesArray[2], botPieces, allPieces))
        moves.addAll(generateRookMoves(playerPiecesArray[3], botPieces, allPieces))
        moves.addAll(generateQueenMoves(playerPiecesArray[4], botPieces, allPieces))
        moves.addAll(generateKingMoves(playerPiecesArray[5], botPieces, playerPieces, false))

        return moves
    }

    fun generateLegalMovesForPlayer(): ArrayDeque<Long> {
        return filterOutIllegalMoves(generateMovesForPlayer(), playerPiecesArray[5], false, playerColor)
    }

    fun generatePawnMoves(pawns: Long, opponentPieces: Long, emptySquares: Long, color: PieceColor): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        val startRowMask: Long = if (color == PieceColor.BLACK) 0x00FF000000000000 else 0x000000000000FF00
        val promotionRowMask: Long = if (color == PieceColor.BLACK) 0x000000000000FF00 else 0x00FF000000000000

        var pawnsCopy = pawns
        while (pawnsCopy != 0L) {
            val position = pawnsCopy.takeLowestOneBit()
            val singleMove = if (color == PieceColor.BLACK) position shr 8 else position shl 8

            val fromIndex = position.countTrailingZeroBits()
            val singleMoveIndex = singleMove.countTrailingZeroBits()

            if ((singleMove and emptySquares) != 0L) {
                if ((singleMove and promotionRowMask) != 0L) {
                    moves.add(encodeMove(fromIndex, singleMoveIndex, determinePiece(fromIndex)))
                } else {
                    moves.add(encodeMove(fromIndex, singleMoveIndex, determinePiece(fromIndex)))

                    if ((position and startRowMask) != 0L) {
                        val intermediateMove = if (color == PieceColor.BLACK) singleMove shr 8 else singleMove shl 8
                        val doubleMove = if (color == PieceColor.BLACK) position shr 16 else position shl 16
                        val doubleMoveIndex = doubleMove.countTrailingZeroBits()
                        if ((doubleMove and emptySquares) != 0L && (intermediateMove and emptySquares) != 0L) {
                            moves.add(encodeMove(fromIndex, doubleMoveIndex, determinePiece(fromIndex)))
                        }
                    }
                }
            }

            val captureLeft = if (color == PieceColor.BLACK) position shr 7 else position shl 7
            val captureRight = if (color == PieceColor.BLACK) position shr 9 else position shl 9
            val captureLeftIndex = captureLeft.countTrailingZeroBits()
            val captureRightIndex = captureRight.countTrailingZeroBits()
            if ((captureLeft and opponentPieces) != 0L && isLegalPosition(captureLeft) && isSameRankOrFile(fromIndex, captureLeftIndex, 7)) {
                moves.add(encodeMove(fromIndex, captureLeftIndex, determinePiece(fromIndex), determinePiece(captureLeftIndex)))
            }
            if ((captureRight and opponentPieces) != 0L && isLegalPosition(captureRight) && isSameRankOrFile(fromIndex, captureRightIndex, 9)) {
                moves.add(encodeMove(fromIndex, captureRightIndex, determinePiece(fromIndex), determinePiece(captureRightIndex)))
            }

            pawnsCopy = pawnsCopy xor position
        }
        return moves
    }

    fun generateKnightMoves(knights: Long, opponentPieces: Long, emptySquares: Long): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        var knightsCopy = knights

        while (knightsCopy != 0L) {
            val position = knightsCopy.takeLowestOneBit()
            val positionIndex = position.countTrailingZeroBits()

            for (offset in knightOffsets) {
                val targetIndex = positionIndex + offset
                if (isLegalKnightMove(positionIndex, targetIndex)) {
                    val target = 1L shl targetIndex
                    if (target and emptySquares != 0L) {
                        moves.add(encodeMove(positionIndex, targetIndex, determinePiece(positionIndex)))
                    } else if (target and opponentPieces != 0L) {
                        moves.add(encodeMove(positionIndex, targetIndex, determinePiece(positionIndex), determinePiece(targetIndex)))
                    }
                }
            }
            knightsCopy = knightsCopy xor position
        }

        return moves
    }

    fun generateBishopMoves(bishops: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(bishops, opponentPieces, allPieces, bishopOffsets)

    fun generateRookMoves(rooks: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(rooks, opponentPieces, allPieces, rookOffsets)

    fun generateQueenMoves(queens: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(queens, opponentPieces, allPieces, queenOffsets)

    //sliding moves such as bishop, rook and queens
    fun generateSlidingMoves(pieces: Long, opponentPieces: Long, allPieces: Long, offsets: IntArray): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        var piecesCopy = pieces

        while (piecesCopy != 0L) {
            val position = piecesCopy.takeLowestOneBit()
            val positionIndex = position.countTrailingZeroBits()

            for (offset in offsets) {
                var targetIndex = positionIndex
                while (true) {
                    targetIndex += offset

                    if (!isWithinBounds(targetIndex) || !isSameRankOrFile(positionIndex, targetIndex, offset)) {
                        break
                    }

                    val target = 1L shl targetIndex

                    if (target and allPieces != 0L) {
                        if (target and opponentPieces != 0L) {
                            moves.add(encodeMove(positionIndex, targetIndex, determinePiece(positionIndex), determinePiece(targetIndex)))
                        }
                        break
                    }
                    moves.add(encodeMove(positionIndex, targetIndex, determinePiece(positionIndex)))
                }
            }

            piecesCopy = piecesCopy xor position
        }

        return moves
    }

    fun generateKingMoves(king: Long, opponentPieces: Long, friendlyPieces: Long, isForBot: Boolean): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        val kingIndex = king.countTrailingZeroBits()
        val canWhiteKingCastle = kingIndex == 4
        val canBlackKingCastle = kingIndex == 60

        for (offset in kingOffsets) {
            val targetIndex = kingIndex + offset
            if (isWithinBounds(targetIndex) && isSameRankOrFile(kingIndex, targetIndex, offset)) {
                val target = 1L shl targetIndex
                if ((target and friendlyPieces) == 0L) {
                    if (target and opponentPieces != 0L) {
                        moves.add(encodeMove(kingIndex, targetIndex, determinePiece(kingIndex), determinePiece(targetIndex)))
                    } else {
                        moves.add(encodeMove(kingIndex, targetIndex, determinePiece(kingIndex)))
                    }
                }
            }
        }

        if (canWhiteKingCastle) {
            if (bitboard.hasCastlingRights(Bitboard.WHITE_KINGSIDE) &&
                bitboard.isSquareEmpty(BitCell.F1.bit) && bitboard.isSquareEmpty(BitCell.G1.bit) &&
                !isSquareUnderAttack(BitCell.E1.bit, isForBot, PieceColor.WHITE) &&
                !isSquareUnderAttack(BitCell.F1.bit, isForBot, PieceColor.WHITE) &&
                !isSquareUnderAttack(BitCell.G1.bit, isForBot, PieceColor.WHITE)) {
                moves.add(encodeMove(BitCell.E1.ordinal, BitCell.G1.ordinal, BitPiece.WHITE_KING.ordinal, isCastling = true))
            }

            if (bitboard.hasCastlingRights(Bitboard.WHITE_QUEENSIDE) &&
                bitboard.isSquareEmpty(BitCell.B1.bit) && bitboard.isSquareEmpty(BitCell.C1.bit) && bitboard.isSquareEmpty(BitCell.D1.bit) &&
                !isSquareUnderAttack(BitCell.E1.bit, isForBot, PieceColor.WHITE) &&
                !isSquareUnderAttack(BitCell.D1.bit, isForBot, PieceColor.WHITE) &&
                !isSquareUnderAttack(BitCell.C1.bit, isForBot, PieceColor.WHITE)) {
                moves.add(encodeMove(BitCell.E1.ordinal, BitCell.C1.ordinal, BitPiece.WHITE_KING.ordinal, isCastling = true))
            }
        } else if (canBlackKingCastle) {
            if (bitboard.hasCastlingRights(Bitboard.BLACK_KINGSIDE) &&
                bitboard.isSquareEmpty(BitCell.F8.bit) && bitboard.isSquareEmpty(BitCell.G8.bit) &&
                !isSquareUnderAttack(BitCell.E8.bit, isForBot, PieceColor.BLACK) &&
                !isSquareUnderAttack(BitCell.F8.bit, isForBot, PieceColor.BLACK) &&
                !isSquareUnderAttack(BitCell.G8.bit, isForBot, PieceColor.BLACK)) {
                moves.add(encodeMove(BitCell.E8.ordinal, BitCell.G8.ordinal, BitPiece.BLACK_KING.ordinal, isCastling = true))
            }

            if (bitboard.hasCastlingRights(Bitboard.BLACK_QUEENSIDE) &&
                bitboard.isSquareEmpty(BitCell.B8.bit) && bitboard.isSquareEmpty(BitCell.C8.bit) && bitboard.isSquareEmpty(BitCell.D8.bit) &&
                !isSquareUnderAttack(BitCell.E8.bit, isForBot, PieceColor.BLACK) &&
                !isSquareUnderAttack(BitCell.D8.bit, isForBot, PieceColor.BLACK) &&
                !isSquareUnderAttack(BitCell.C8.bit, isForBot, PieceColor.BLACK)) {
                moves.add(encodeMove(BitCell.E8.ordinal, BitCell.C8.ordinal, BitPiece.BLACK_KING.ordinal, isCastling = true))
            }
        }

        return moves
    }

    fun filterOutIllegalMoves(moves: ArrayDeque<Long>, king: Long, isForBot: Boolean, color: PieceColor): ArrayDeque<Long> {
        val legalMoves = ArrayDeque<Long>()
        val originalBitboard = bitboard.copy()

        for (move in moves) {
            val decodedMove = decodeMove(move)
            bitboard.movePiece(decodedMove)

            val kingPosition = if (decodedMove.piece == BitPiece.WHITE_KING || decodedMove.piece == BitPiece.BLACK_KING) {
                decodedMove.to
            } else {
                king
            }

            if (!isSquareUnderAttack(kingPosition, isForBot, color)) {
                legalMoves.add(move)
            }
            bitboard.restore(originalBitboard)
        }

        return legalMoves
    }

    fun isSquareUnderAttack(square: Long, isForBot: Boolean, color: PieceColor): Boolean {
        updateBoards()
        val opponentPawns = if (isForBot) playerPiecesArray[0] else botPiecesArray[0]
        val opponentKnights = if (isForBot) playerPiecesArray[1] else botPiecesArray[1]
        val opponentBishops = if (isForBot) playerPiecesArray[2] else botPiecesArray[2]
        val opponentRooks = if (isForBot) playerPiecesArray[3] else botPiecesArray[3]
        val opponentQueens = if (isForBot) playerPiecesArray[4] else botPiecesArray[4]
        val opponentKing = if (isForBot) playerPiecesArray[5] else botPiecesArray[5]

        val pawnAttacks = getPawnAttackMask(opponentPawns, color)
        val knightAttacks = getKnightAttackMask(opponentKnights)
        val bishopAttacks = getBishopAttackMask(opponentBishops, allPieces)
        val rookAttacks = getRookAttackMask(opponentRooks, allPieces)
        val queenAttacks = getQueenAttackMask(opponentQueens, allPieces)
        val kingAttacks = getKingAttackMask(opponentKing)

        val allAttacks = pawnAttacks or knightAttacks or bishopAttacks or rookAttacks or queenAttacks or kingAttacks
        return (square and allAttacks) != 0L
    }

    fun getPawnAttackMask(pawns: Long, color: PieceColor): Long {
        var attacks = 0L
        var pawnsCopy = pawns

        while (pawnsCopy != 0L) {
            val position = pawnsCopy.takeLowestOneBit()
            if (color == PieceColor.BLACK) {
                attacks = attacks or (position shl 7) or (position shl 9)
            } else {
                attacks = attacks or (position shr 7) or (position shr 9)
            }
            pawnsCopy = pawnsCopy xor position
        }

        return attacks
    }

    fun getKnightAttackMask(knights: Long): Long {
        var attacks = 0L
        var knightsCopy = knights

        while (knightsCopy != 0L) {
            val position = knightsCopy.takeLowestOneBit()
            val positionIndex = position.countTrailingZeroBits()

            for (offset in knightOffsets) {
                val targetIndex = positionIndex + offset
                if (isWithinBounds(targetIndex)) {
                    attacks = attacks or (1L shl targetIndex)
                }
            }
            knightsCopy = knightsCopy xor position
        }
        return attacks
    }

    fun getSlidingPieceAttackMask(pieces: Long, allPieces: Long, offsets: IntArray): Long {
        var attacks = 0L
        var piecesCopy = pieces

        while (piecesCopy != 0L) {
            val position = piecesCopy.takeLowestOneBit()
            val positionIndex = position.countTrailingZeroBits()

            for (offset in offsets) {
                var targetIndex = positionIndex
                while (true) {
                    targetIndex += offset
                    if (!isWithinBounds(targetIndex) || !isSameRankOrFile(positionIndex, targetIndex, offset)) break
                    val target = 1L shl targetIndex
                    attacks = attacks or target
                    if ((target and allPieces) != 0L) break
                }
            }
            piecesCopy = piecesCopy xor position
        }
        return attacks
    }

    fun getBishopAttackMask(bishops: Long, allPieces: Long): Long {
        return getSlidingPieceAttackMask(bishops, allPieces, bishopOffsets)
    }

    fun getRookAttackMask(rooks: Long, allPieces: Long): Long {
        return getSlidingPieceAttackMask(rooks, allPieces, rookOffsets)
    }

    fun getQueenAttackMask(queens: Long, allPieces: Long): Long {
        return getSlidingPieceAttackMask(queens, allPieces, queenOffsets)
    }

    fun getKingAttackMask(king: Long): Long {
        var attacks = 0L
        val kingIndex = king.countTrailingZeroBits()

        for (offset in kingOffsets) {
            val targetIndex = kingIndex + offset
            if (isWithinBounds(targetIndex)) {
                attacks = attacks or (1L shl targetIndex)
            }
        }
        return attacks
    }

    fun determinePiece(index: Int): Int {
        for (piece in bitboard.bitboards.indices) {
            if ((bitboard.bitboards[piece] and (1L shl index)) != 0L) {
                return piece
            }
        }
        return 0
    }

    fun isLegalPosition(position: Long): Boolean {
        return position != 0L && position and (position - 1) == 0L
    }

    fun isLegalKnightMove(fromIndex: Int, toIndex: Int): Boolean {
        if (toIndex !in 0..63) return false

        val fromFile = fromIndex % 8
        val toFile = toIndex % 8
        val fileDiff = abs(fromFile - toFile)

        return fileDiff == 1 || fileDiff == 2
    }

    fun isSameRankOrFile(fromIndex: Int, toIndex: Int, direction: Int): Boolean {
        val fromFile = fromIndex % 8
        val toFile = toIndex % 8
        val fromRank = fromIndex / 8
        val toRank = toIndex / 8

        return when (direction) {
            1, -1 -> fromRank == toRank
            8, -8 -> fromFile == toFile
            7, -7 -> abs(fromFile - toFile) == abs(fromRank - toRank)
            9, -9 -> abs(fromFile - toFile) == abs(fromRank - toRank)
            else -> false
        }
    }

    fun isWithinBounds(index: Int): Boolean {
        return index in 0..63
    }
}