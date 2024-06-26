package com.example.chessmate.util.chess.bitboard

import kotlin.math.abs

class BitboardMoveGenerator(private val bitboard: Bitboard) {

    companion object {
        val knightOffsets = listOf(15, 6, 10, 17, -17, -10, -6, -15)
        val bishopOffsets = listOf(9, -9, 7, -7)
        val rookOffsets = listOf(1, -1, 8, -8)
        val queenOffsets = listOf(1, -1, 7, -7, 8, -8, 9, -9)
        val kingOffsets = listOf(1, -1, 7, -7, 8, -8, 9, -9)

        fun isLegalPawnMove(position: Long): Boolean {
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

    fun generateAllLegalMoves(): ArrayDeque<Long>  {
        val allPieces = bitboard.whitePawns or bitboard.whiteKnights or
                bitboard.whiteBishops or bitboard.whiteRooks or
                bitboard.whiteQueens or bitboard.whiteKing or
                bitboard.blackPawns or bitboard.blackKnights or
                bitboard.blackBishops or bitboard.blackRooks or
                bitboard.blackQueens or bitboard.blackKing
        val emptySquares = allPieces.inv()
        val whitePieces = bitboard.whitePawns or bitboard.whiteKnights or
                bitboard.whiteBishops or bitboard.whiteRooks or
                bitboard.whiteQueens or bitboard.whiteKing
        val blackPieces = bitboard.blackPawns or bitboard.blackKnights or
                bitboard.blackBishops or bitboard.blackRooks or
                bitboard.blackQueens or bitboard.blackKing

        val moves = ArrayDeque<Long>()

        moves.addAll(generatePawnMoves(bitboard.whitePawns, blackPieces, emptySquares, false))
        moves.addAll(generatePawnMoves(bitboard.blackPawns, whitePieces, emptySquares, true))
        moves.addAll(generateKnightMoves(bitboard.whiteKnights, blackPieces, emptySquares))
        moves.addAll(generateKnightMoves(bitboard.blackKnights, whitePieces, emptySquares))
        moves.addAll(generateBishopMoves(bitboard.whiteBishops, allPieces, blackPieces))
        moves.addAll(generateBishopMoves(bitboard.blackBishops, allPieces, whitePieces))
        moves.addAll(generateRookMoves(bitboard.whiteRooks, allPieces, blackPieces))
        moves.addAll(generateRookMoves(bitboard.blackRooks, allPieces, whitePieces))
        moves.addAll(generateQueenMoves(bitboard.whiteQueens, allPieces, blackPieces))
        moves.addAll(generateQueenMoves(bitboard.blackQueens, allPieces, whitePieces))

        return moves
    }

    fun generateLegalMoveForBot() {}

    fun generateLegalMovesForPlayer(){}

    fun generatePawnMoves(pawns: Long, opponentPieces: Long, emptySquares: Long, isForBot: Boolean): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        val startRowMask: Long = if (isForBot) 0x00FF000000000000 else 0x000000000000FF00
        val promotionRowMask: Long = if (isForBot) 0x000000000000FF00 else 0x00FF000000000000

        var pawnsCopy = pawns
        while (pawnsCopy != 0L) {
            val position = pawnsCopy.takeLowestOneBit()
            val singleMove = if (isForBot) position shr 8 else position shl 8

            if ((singleMove and emptySquares) != 0L) {
                if ((singleMove and promotionRowMask) != 0L) {
                    moves.add(singleMove)
                } else {
                    moves.add(singleMove)

                    if ((position and startRowMask) != 0L) {
                        val intermediateMove = if (isForBot) singleMove shr 8 else singleMove shl 8
                        val doubleMove = if (isForBot) position shr 16 else position shl 16
                        if ((doubleMove and emptySquares) != 0L && (intermediateMove and emptySquares) != 0L) {
                            moves.add(doubleMove)
                        }
                    }
                }
            }

            val captureLeft = if (isForBot) position shr 7 else position shl 7
            val captureRight = if (isForBot) position shr 9 else position shl 9
            if ((captureLeft and opponentPieces) != 0L && isLegalPawnMove(captureLeft)) {
                moves.add(captureLeft)
            }
            if ((captureRight and opponentPieces) != 0L && isLegalPawnMove(captureRight)) {
                moves.add(captureRight)
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
            val positionIndex = knightsCopy.countTrailingZeroBits()

            for (offset in knightOffsets) {
                val targetIndex = positionIndex + offset
                if (isLegalKnightMove(positionIndex, targetIndex)) {
                    val target = 1L shl targetIndex
                    if ((target and emptySquares != 0L) || (target and opponentPieces) != 0L) {
                        moves.add(target)
                    }
                }
            }
            knightsCopy = knightsCopy xor position
        }

        return moves
    }

    fun generateBishopMoves(bishops: Long, allPieces: Long, opponentPieces: Long): ArrayDeque<Long> = generateSlidingMoves(bishops, allPieces, opponentPieces, bishopOffsets)

    fun generateRookMoves(rooks: Long, allPieces: Long, opponentPieces: Long): ArrayDeque<Long> = generateSlidingMoves(rooks, allPieces, opponentPieces, rookOffsets)

    fun generateQueenMoves(queens: Long, allPieces: Long, opponentPieces: Long): ArrayDeque<Long> = generateSlidingMoves(queens, allPieces, opponentPieces, queenOffsets)

    //sliding moves such as bishop, rook and queens
    fun generateSlidingMoves(pieces: Long, allPieces: Long, opponentPieces: Long, directions: List<Int>): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        var piecesCopy = pieces

        while (piecesCopy != 0L) {
            val position = piecesCopy.takeLowestOneBit()
            val positionIndex = piecesCopy.countTrailingZeroBits()

            for (direction in directions) {
                var targetIndex = positionIndex
                while (true) {
                    targetIndex += direction

                    if (!isWithinBounds(targetIndex) || !isSameRankOrFile(positionIndex, targetIndex, direction)) {
                        break
                    }

                    val target = 1L shl targetIndex

                    if (target and allPieces != 0L) {
                        if (target and opponentPieces != 0L) {
                            moves.add(target)
                        }
                        break
                    }
                    moves.add(target)
                }
            }

            piecesCopy = piecesCopy xor position
        }

        return moves
    }
}