package com.example.chessmate.util.chess.bitboard

class BitboardMoveGenerator(private val bitboard: Bitboard) {

    companion object {
        val knightOffsets = listOf(15, 6, 10, 17, -17, -10, -6, -15)
        val bishopOffsets = listOf(9, -9, 7, -7)
        val rookOffsets = listOf(1, -1, 8, -8)
        val queenOffsets = knightOffsets + rookOffsets
        val kingOffsets = listOf(1, -1, 8, -8, 9, -9, 7, -7)

        fun isLegalPosition(position: Long): Boolean {
            return position != 0L && position and (position - 1) == 0L
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
}