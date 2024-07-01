package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.chessboard.PieceColor
import kotlin.math.abs

class BitboardMoveGenerator (private val bitboard: Bitboard, private val playerColor: PieceColor, private val botColor: PieceColor) {
    private val knightOffsets = intArrayOf(15, 6, 10, 17, -17, -10, -6, -15)
    private val bishopOffsets = intArrayOf(9, -9, 7, -7)
    private val rookOffsets = intArrayOf(1, -1, 8, -8)
    private val queenOffsets = intArrayOf(1, -1, 7, -7, 8, -8, 9, -9)
    private val kingOffsets = intArrayOf(1, -1, 7, -7, 8, -8, 9, -9)

    private var allPieces: Long = bitboard.whitePawns or bitboard.whiteKnights or
            bitboard.whiteBishops or bitboard.whiteRooks or
            bitboard.whiteQueens or bitboard.whiteKing or
            bitboard.blackPawns or bitboard.blackKnights or
            bitboard.blackBishops or bitboard.blackRooks or
            bitboard.blackQueens or bitboard.blackKing

    private var emptySquares: Long = allPieces.inv()

    private var playerPawns = if (playerColor == PieceColor.WHITE) bitboard.whitePawns else bitboard.blackPawns
    private var playerKnights = if (playerColor == PieceColor.WHITE) bitboard.whiteKnights else bitboard.blackKnights
    private var playerBishops = if (playerColor == PieceColor.WHITE) bitboard.whiteBishops else bitboard.blackBishops
    private var playerRooks = if (playerColor == PieceColor.WHITE) bitboard.whiteRooks else bitboard.blackRooks
    private var playerQueens = if (playerColor == PieceColor.WHITE) bitboard.whiteQueens else bitboard.blackQueens
    private var playerKing = if (playerColor == PieceColor.WHITE) bitboard.whiteKing else bitboard.blackKing
    private var playerPieces = playerPawns or playerKnights or
            playerBishops or playerRooks or
            playerQueens or playerKing

    private var botPawns = if (botColor == PieceColor.WHITE) bitboard.whitePawns else bitboard.blackPawns
    private var botKnights = if (botColor == PieceColor.WHITE) bitboard.whiteKnights else bitboard.blackKnights
    private var botBishops = if (botColor == PieceColor.WHITE) bitboard.whiteBishops else bitboard.blackBishops
    private var botRooks = if (botColor == PieceColor.WHITE) bitboard.whiteRooks else bitboard.blackRooks
    private var botQueens = if (botColor == PieceColor.WHITE) bitboard.whiteQueens else bitboard.blackQueens
    private var botKing = if (botColor == PieceColor.WHITE) bitboard.whiteKing else bitboard.blackKing
    private var botPieces = botPawns or botKnights or
            botBishops or botRooks or
            botQueens or botKing

    fun updateBoards() {
        allPieces = bitboard.whitePawns or bitboard.whiteKnights or
                bitboard.whiteBishops or bitboard.whiteRooks or
                bitboard.whiteQueens or bitboard.whiteKing or
                bitboard.blackPawns or bitboard.blackKnights or
                bitboard.blackBishops or bitboard.blackRooks or
                bitboard.blackQueens or bitboard.blackKing

        emptySquares = allPieces.inv()

        playerPawns = if (playerColor == PieceColor.WHITE) bitboard.whitePawns else bitboard.blackPawns
        playerKnights = if (playerColor == PieceColor.WHITE) bitboard.whiteKnights else bitboard.blackKnights
        playerBishops = if (playerColor == PieceColor.WHITE) bitboard.whiteBishops else bitboard.blackBishops
        playerRooks = if (playerColor == PieceColor.WHITE) bitboard.whiteRooks else bitboard.blackRooks
        playerQueens = if (playerColor == PieceColor.WHITE) bitboard.whiteQueens else bitboard.blackQueens
        playerKing = if (playerColor == PieceColor.WHITE) bitboard.whiteKing else bitboard.blackKing
        playerPieces = playerPawns or playerKnights or
                playerBishops or playerRooks or
                playerQueens or playerKing

        botPawns = if (botColor == PieceColor.WHITE) bitboard.whitePawns else bitboard.blackPawns
        botKnights = if (botColor == PieceColor.WHITE) bitboard.whiteKnights else bitboard.blackKnights
        botBishops = if (botColor == PieceColor.WHITE) bitboard.whiteBishops else bitboard.blackBishops
        botRooks = if (botColor == PieceColor.WHITE) bitboard.whiteRooks else bitboard.blackRooks
        botQueens = if (botColor == PieceColor.WHITE) bitboard.whiteQueens else bitboard.blackQueens
        botKing = if (botColor == PieceColor.WHITE) bitboard.whiteKing else bitboard.blackKing
        botPieces = botPawns or botKnights or
                botBishops or botRooks or
                botQueens or botKing
    }

    fun generateAllLegalMoves(): ArrayDeque<Long>  {
        updateBoards()
        val moves = ArrayDeque<Long>()

        //moves.addAll(generatePawnMoves(playerPawns, botPieces, emptySquares, false))
        //moves.addAll(generatePawnMoves(botPawns, playerPieces, emptySquares, true))
        //moves.addAll(generateKnightMoves(playerKnights, botPieces, emptySquares))
        //moves.addAll(generateKnightMoves(botKnights, playerPieces, emptySquares))
        //moves.addAll(generateBishopMoves(playerBishops, botPieces, allPieces))
        //moves.addAll(generateBishopMoves(botBishops, playerPieces, allPieces))
        //moves.addAll(generateRookMoves(playerRooks, botPieces, allPieces))
        //moves.addAll(generateRookMoves(botRooks, playerPieces, allPieces))
        //moves.addAll(generateQueenMoves(playerQueens, botPieces, allPieces))
        //moves.addAll(generateQueenMoves(botQueens, playerPieces, allPieces))
        //moves.addAll(generateKingMoves(playerKing, playerPieces))
        moves.addAll(generateKingMoves(botKing, botPieces))

        return moves
    }

    fun generateLegalMovesForBot(): ArrayDeque<Long> {
        updateBoards()
        val moves = ArrayDeque<Long>()

        moves.addAll(generatePawnMoves(botPawns, playerPieces, emptySquares, true))
        moves.addAll(generateKnightMoves(botKnights, playerPieces, emptySquares))
        moves.addAll(generateBishopMoves(botBishops, playerPieces, allPieces))
        moves.addAll(generateRookMoves(botRooks, playerPieces, allPieces))
        moves.addAll(generateQueenMoves(botQueens, playerPieces, allPieces))
        moves.addAll(generateKingMoves(botKing, botPieces))

        return moves
    }

    fun generateLegalMovesForPlayer(): ArrayDeque<Long> {
        updateBoards()
        val moves = ArrayDeque<Long>()

        moves.addAll(generatePawnMoves(playerPawns, botPieces, emptySquares, false))
        moves.addAll(generateKnightMoves(playerKnights, botPieces, emptySquares))
        moves.addAll(generateBishopMoves(playerBishops, botPieces, allPieces))
        moves.addAll(generateRookMoves(playerRooks, botPieces, allPieces))
        moves.addAll(generateQueenMoves(playerQueens, botPieces, allPieces))
        moves.addAll(generateKingMoves(playerKing, playerPieces))

        return moves
    }

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
            if ((captureLeft and opponentPieces) != 0L && isLegalPosition(captureLeft)) {
                moves.add(captureLeft)
            }
            if ((captureRight and opponentPieces) != 0L && isLegalPosition(captureRight)) {
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

    fun generateBishopMoves(bishops: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(bishops, opponentPieces, allPieces, bishopOffsets)

    fun generateRookMoves(rooks: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(rooks, opponentPieces, allPieces, rookOffsets)

    fun generateQueenMoves(queens: Long, opponentPieces: Long, allPieces: Long): ArrayDeque<Long> = generateSlidingMoves(queens, opponentPieces, allPieces, queenOffsets)

    //sliding moves such as bishop, rook and queens
    fun generateSlidingMoves(pieces: Long, opponentPieces: Long, allPieces: Long, offsets: IntArray): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()
        var piecesCopy = pieces

        while (piecesCopy != 0L) {
            val position = piecesCopy.takeLowestOneBit()
            val positionIndex = piecesCopy.countTrailingZeroBits()

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

    fun generateKingMoves(king: Long, friendlyPieces: Long): ArrayDeque<Long> {
        val moves = ArrayDeque<Long>()

        for (offset in kingOffsets) {
            val target = if (offset > 0) king shl offset else king shr -offset
            if (isLegalPosition(target) && (target and friendlyPieces) == 0L) {
                moves.add(target)
            }
        }

        return moves
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