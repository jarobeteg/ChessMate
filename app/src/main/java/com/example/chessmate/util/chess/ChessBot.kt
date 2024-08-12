package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardEvaluator
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator

class ChessBot(val color: PieceColor){

    fun findBestMove(bitboard: Bitboard, depth: Int, maximizingPlayer: Boolean): BitMove? {
        return alphaBeta(bitboard, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maximizingPlayer, color).second
    }

    private fun alphaBeta(board: Bitboard, depth: Int, alpha: Float, beta: Float, maximizingPlayer: Boolean, currentColor: PieceColor): Pair<Float, BitMove?> {
        if (depth == 0 || board.isGameEnded()) {
            val evaluator = BitboardEvaluator(board)
            return Pair(evaluator.evaluate(), null)
        }

        val moveGenerator = BitboardMoveGenerator(board)
        val legalMoves = moveGenerator.generateLegalMovesForAlphaBeta(maximizingPlayer)
        //this means checkmate or stalemate
        //checkmate if the king is in check and has no legal moves, and to block the check, and to remove the piece giving the check
        //stalemate if the king is not in check and has no legal moves
        if (legalMoves.isEmpty()) {
            val evaluator = BitboardEvaluator(board)
            return Pair(evaluator.evaluate(), null)
        }

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

        if (maximizingPlayer) {
            val topMoves = sortedMoves.take(3)
            var bestValue = Float.NEGATIVE_INFINITY
            for (move in topMoves) {
                val newBoard = board.copy().apply { movePiece(move) }
                val (value, _) = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, false, currentColor.opposite())
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
            val topMoves = sortedMoves.reversed().take(3)
            var bestValue = Float.POSITIVE_INFINITY
            for (move in topMoves) {
                val newBoard = board.copy().apply { movePiece(move) }
                val (value, _) = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, true, currentColor.opposite())
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