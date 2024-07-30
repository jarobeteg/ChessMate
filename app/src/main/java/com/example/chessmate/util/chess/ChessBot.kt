package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardEvaluator
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator
import com.example.chessmate.util.chess.chessboard.PieceColor

class ChessBot(val color: PieceColor){

    fun findBestMove(bitboard: Bitboard, depth: Int, maximizingPlayer: Boolean): BitMove? {
        return alphaBeta(bitboard, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maximizingPlayer, color).second
    }

    private fun alphaBeta(board: Bitboard, depth: Int, alpha: Float, beta: Float, maximizingPlayer: Boolean, currentColor: PieceColor): Pair<Float, BitMove?> {
        if (depth == 0) {
            val evaluator = BitboardEvaluator(board)
            return Pair(evaluator.evaluate(), null)
        }

        val moveGenerator = BitboardMoveGenerator(board, color.opposite(), color)
        val legalMoves = moveGenerator.generateLegalMovesForAlphaBeta(maximizingPlayer)
        val evaluatedMoves = legalMoves.map { move ->
            val decodedMove = BitboardMoveGenerator.decodeMove(move)
            val newBoard = board.copy().apply { movePiece(decodedMove) }
            val evaluator = BitboardEvaluator(newBoard)
            Pair(decodedMove, evaluator.evaluate())
        }

        val sortedMoves = evaluatedMoves.sortedByDescending { it.second }.map { it.first }

        var localAlpha = alpha
        var localBeta = beta
        var bestMove: BitMove? = null

        val topMoves = sortedMoves.reversed().take(3)

        if (maximizingPlayer) {
            var bestValue = Float.NEGATIVE_INFINITY
            for (move in topMoves) {
                val newBoard = board.copy().apply { movePiece(move) }
                val (value, _) = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, false, color.opposite())
                if (value > bestValue) {
                    bestValue = value
                    if (currentColor == color) {
                        bestMove = move
                    }
                }
                localAlpha = maxOf(localAlpha, bestValue)
                if (localBeta <= localAlpha) break
            }
            return Pair(bestValue, bestMove)
        } else {
            var bestValue = Float.POSITIVE_INFINITY
            for (move in topMoves) {
                val newBoard = board.copy().apply { movePiece(move) }
                val (value, _) = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, true, color.opposite())
                if (value < bestValue) {
                    bestValue = value
                    if (currentColor == color) {
                        bestMove = move
                    }
                }
                localBeta = minOf(localBeta, bestValue)
                if (localBeta <= localAlpha) break
            }
            return Pair(bestValue, bestMove)
        }
    }
}