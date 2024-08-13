package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.GamePhase
import com.example.chessmate.util.chess.PestoEvalTables
import com.example.chessmate.util.chess.PieceColor
import java.math.BigDecimal
import java.math.RoundingMode

class BitboardEvaluator(private val bitboard: Bitboard) {

    companion object {
        const val PAWN_VALUE = 1.0F
        const val KNIGHT_VALUE = 3.0F
        const val BISHOP_VALUE = 3.0F
        const val ROOK_VALUE = 5.0F
        const val QUEEN_VALUE = 9.0F
        const val KING_VALUE = 0.0F
        const val MATE_SCORE = 10000.0F
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

        val isPlayerMated = bitboard.isPlayerCheckmated()
        val isBotMated = bitboard.isBotCheckmated()

        score += evaluateMaterial()
        score += evaluatePesto()
        score += evaluateMateScore(isPlayerMated, isBotMated)

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

    private fun evaluateMateScore(isPlayerMated: Boolean, isBotMated: Boolean): Float {
        return when {
            isPlayerMated -> -MATE_SCORE
            isBotMated -> MATE_SCORE
            else -> 0.0F
        }
    }

    private fun evaluatePesto(): Float {
        var pestoScore = BigDecimal.ZERO

        val playerOffset = if (GameContext.playerColor == PieceColor.WHITE) 0 else 6
        val botOffset = if (GameContext.botColor == PieceColor.WHITE) 0 else 6

        val isMidGame = GameContext.gamePhase == GamePhase.OPENING || GameContext.gamePhase == GamePhase.MIDGAME

        fun evaluatePieces(bitboard: Long, getValue: (Int, Boolean) -> Float, isForBot: Boolean) {
            var pieces = bitboard
            while (pieces != 0L) {
                val position = pieces.takeLowestOneBit()
                val index = position.countTrailingZeroBits()
                val isBlack = if (isForBot) GameContext.botColor == PieceColor.BLACK else GameContext.playerColor == PieceColor.BLACK
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