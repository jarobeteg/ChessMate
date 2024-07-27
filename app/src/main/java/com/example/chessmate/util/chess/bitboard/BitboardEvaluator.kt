package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.PieceSquareTables

class BitboardEvaluator(private val bitboard: Bitboard) {

    companion object {
        const val PAWN_VALUE = 1.0F
        const val KNIGHT_VALUE = 3.0F
        const val BISHOP_VALUE = 3.0F
        const val ROOK_VALUE = 5.0F
        const val QUEEN_VALUE = 9.0F
        const val KING_VALUE = 0.0F
    }

    private val pst = PieceSquareTables()
    private val pieceValues = mapOf(
        BitPiece.WHITE_PAWN to PAWN_VALUE,
        BitPiece.WHITE_KNIGHT to KNIGHT_VALUE,
        BitPiece.WHITE_BISHOP to BISHOP_VALUE,
        BitPiece.WHITE_ROOK to ROOK_VALUE,
        BitPiece.WHITE_QUEEN to QUEEN_VALUE,
        BitPiece.WHITE_KING to KING_VALUE,
        BitPiece.BLACK_PAWN to -PAWN_VALUE,
        BitPiece.BLACK_KNIGHT to -KNIGHT_VALUE,
        BitPiece.BLACK_BISHOP to -BISHOP_VALUE,
        BitPiece.BLACK_ROOK to -ROOK_VALUE,
        BitPiece.BLACK_QUEEN to -QUEEN_VALUE,
        BitPiece.BLACK_KING to -KING_VALUE
    )

    fun evaluate(): Float {
        var score = 0.0F

        score += evaluateMaterial()
        score += evaluatePST()

        return score
    }

    private fun evaluateMaterial(): Float {
        var materialScore = 0.0F

        for ((piece, value) in pieceValues) {
            materialScore += countBits(bitboard.bitboards[piece.ordinal]) * value
        }

        return materialScore
    }

    private fun countBits(bitboard: Long): Int {
        return java.lang.Long.bitCount(bitboard)
    }

    private fun evaluatePST(): Float {
        var pstScore = 0.0F

        fun evaluateWhitePieces(bitboard: Long, getValue: (Int, Boolean) -> Float) {
            var pieces = bitboard
            while (pieces != 0L) {
                val position = pieces.takeLowestOneBit()
                val index = position.countTrailingZeroBits()
                pstScore += getValue(index, false)
                pieces = pieces xor position
            }
        }

        fun evaluateBlackPieces(bitboard: Long, getValue: (Int, Boolean) -> Float) {
            var pieces = bitboard
            while (pieces != 0L) {
                val position = pieces.takeLowestOneBit()
                val index = position.countTrailingZeroBits()
                pstScore += getValue(index, true) * (-1.0F)
                pieces = pieces xor position
            }
        }

        evaluateWhitePieces(bitboard.bitboards[0], pst::getPawnValue)
        evaluateWhitePieces(bitboard.bitboards[1], pst::getKnightValue)
        evaluateWhitePieces(bitboard.bitboards[2], pst::getBishopValue)
        evaluateWhitePieces(bitboard.bitboards[3], pst::getRookValue)
        evaluateWhitePieces(bitboard.bitboards[4], pst::getQueenValue)
        evaluateWhitePieces(bitboard.bitboards[5], pst::getKingMidGameValue)
        evaluateBlackPieces(bitboard.bitboards[6], pst::getPawnValue)
        evaluateBlackPieces(bitboard.bitboards[7], pst::getKnightValue)
        evaluateBlackPieces(bitboard.bitboards[8], pst::getBishopValue)
        evaluateBlackPieces(bitboard.bitboards[9], pst::getRookValue)
        evaluateBlackPieces(bitboard.bitboards[10], pst::getQueenValue)
        evaluateBlackPieces(bitboard.bitboards[11], pst::getKingMidGameValue)

        return pstScore
    }
}