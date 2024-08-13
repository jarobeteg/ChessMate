package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardEvaluator
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator

class ChessBot(val color: PieceColor){
    private val cache = mutableMapOf<String, Pair<Float, BitMove?>>()

    fun findBestMove(bitboard: Bitboard, depth: Int, maximizingPlayer: Boolean): BitMove? {
        return alphaBeta(bitboard, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, maximizingPlayer, color).second
    }

    private fun alphaBeta(board: Bitboard, depth: Int, alpha: Float, beta: Float, maximizingPlayer: Boolean, currentColor: PieceColor): Pair<Float, BitMove?> {
        val cacheKey = board.toString()

        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey]!!
        }

        if (depth == 0 || board.isGameEnded()) {
            val evaluator = BitboardEvaluator(board)
            val evaluation = Pair(evaluator.evaluate(), null)
            cache[cacheKey] = evaluation
            return evaluation
        }

        val moveGenerator = BitboardMoveGenerator(board)
        val legalMoves = moveGenerator.generateLegalMovesForAlphaBeta(maximizingPlayer)
        //this means checkmate or stalemate
        //checkmate if the king is in check and has no legal moves, and to block the check, and to remove the piece giving the check
        //stalemate if the king is not in check and has no legal moves
        if (legalMoves.isEmpty()) {
            val evaluator = BitboardEvaluator(board)
            val evaluation = Pair(evaluator.evaluate(), null)
            cache[cacheKey] = evaluation
            return evaluation
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
                val newCacheKey = newBoard.toString()

                val (value, _) = if (cache.containsKey(newCacheKey)) {
                    cache[newCacheKey]!!
                } else {
                    val result = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, false, currentColor.opposite())
                    cache[newCacheKey] = result
                    result
                }

                if (value > bestValue) {
                    bestValue = value
                    if (currentColor == color) {
                        bestMove = move
                    }
                }
                localAlpha = maxOf(localAlpha, bestValue)
                if (localBeta <= localAlpha) break
            }
            val result = Pair(bestValue, bestMove)
            cache[cacheKey] = result
            return result
        } else {
            val topMoves = sortedMoves.reversed().take(3)
            var bestValue = Float.POSITIVE_INFINITY
            for (move in topMoves) {
                val newBoard = board.copy().apply { movePiece(move) }
                val newCacheKey = newBoard.toString()

                val (value, _) = if (cache.containsKey(newCacheKey)) {
                    cache[newCacheKey]!!
                } else {
                    val result = alphaBeta(newBoard, depth - 1, localAlpha, localBeta, true, currentColor.opposite())
                    cache[newCacheKey] = result
                    result
                }

                if (value < bestValue) {
                    bestValue = value
                    if (currentColor == color) {
                        bestMove = move
                    }
                }
                localBeta = minOf(localBeta, bestValue)
                if (localBeta <= localAlpha) break
            }
            val result = Pair(bestValue, bestMove)
            cache[cacheKey] = result
            return result
        }
    }
}