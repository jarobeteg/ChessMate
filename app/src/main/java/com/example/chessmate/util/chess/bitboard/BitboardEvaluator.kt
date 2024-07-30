package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.GamePhase
import com.example.chessmate.util.chess.PestoEvalTables
import com.example.chessmate.util.chess.chessboard.PieceColor
import java.math.BigDecimal
import java.math.RoundingMode

class BitboardEvaluator(private val bitboard: Bitboard, private val playerColor: PieceColor, private val botColor: PieceColor) {

    companion object {
        const val PAWN_VALUE = 1.0F
        const val KNIGHT_VALUE = 3.0F
        const val BISHOP_VALUE = 3.0F
        const val ROOK_VALUE = 5.0F
        const val QUEEN_VALUE = 9.0F
        const val KING_VALUE = 0.0F
    }

    private val pesto = PestoEvalTables()
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
        score += evaluatePesto()

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

    private fun evaluatePesto(): Float {
        var pestoScore = BigDecimal.ZERO

        val playerOffset = if (playerColor == PieceColor.WHITE) 0 else 6
        val botOffset = if (botColor == PieceColor.WHITE) 0 else 6

        val isMidGame = bitboard.getGamePhase() == GamePhase.OPENING || bitboard.getGamePhase() == GamePhase.MIDGAME

        fun evaluatePieces(bitboard: Long, getValue: (Int, Boolean) -> Float, isForBot: Boolean) {
            var pieces = bitboard
            while (pieces != 0L) {
                val position = pieces.takeLowestOneBit()
                val index = position.countTrailingZeroBits()
                val isBlack = if (isForBot) botColor == PieceColor.BLACK else playerColor == PieceColor.BLACK
                var score = getValue(index, isBlack)
                if (isBlack) {
                    score *= -1.0F
                }
                val roundedScore = BigDecimal(score.toString()).setScale(2, RoundingMode.DOWN)
                pestoScore = pestoScore.add(roundedScore)
                pieces = pieces xor position
            }
        }

        fun evaluatePlayerPieces() {
            evaluatePieces(bitboard.bitboards[playerOffset + 0], if (isMidGame) pesto::getMidGamePawnValue else pesto::getEndGamePawnValue, false)
            evaluatePieces(bitboard.bitboards[playerOffset + 1], if (isMidGame) pesto::getMidGameKnightValue else pesto::getEndGameKnightValue, false)
            evaluatePieces(bitboard.bitboards[playerOffset + 2], if (isMidGame) pesto::getMidGameBishopValue else pesto::getEndGameBishopValue, false)
            evaluatePieces(bitboard.bitboards[playerOffset + 3], if (isMidGame) pesto::getMidGameRookValue else pesto::getEndGameRookValue, false)
            evaluatePieces(bitboard.bitboards[playerOffset + 4], if (isMidGame) pesto::getMidGameQueenValue else pesto::getEndGameQueenValue, false)
            evaluatePieces(bitboard.bitboards[playerOffset + 5], if (isMidGame) pesto::getMidGameKingValue else pesto::getEndGameKingValue, false)
        }

        fun evaluateBotPieces() {
            evaluatePieces(bitboard.bitboards[botOffset + 0], if (isMidGame) pesto::getMidGamePawnValue else pesto::getEndGamePawnValue, true)
            evaluatePieces(bitboard.bitboards[botOffset + 1], if (isMidGame) pesto::getMidGameKnightValue else pesto::getEndGameKnightValue, true)
            evaluatePieces(bitboard.bitboards[botOffset + 2], if (isMidGame) pesto::getMidGameBishopValue else pesto::getEndGameBishopValue, true)
            evaluatePieces(bitboard.bitboards[botOffset + 3], if (isMidGame) pesto::getMidGameRookValue else pesto::getEndGameRookValue, true)
            evaluatePieces(bitboard.bitboards[botOffset + 4], if (isMidGame) pesto::getMidGameQueenValue else pesto::getEndGameQueenValue, true)
            evaluatePieces(bitboard.bitboards[botOffset + 5], if (isMidGame) pesto::getMidGameKingValue else pesto::getEndGameKingValue, true)
        }

        evaluatePlayerPieces()
        evaluateBotPieces()

        return pestoScore.toFloat()
    }
}