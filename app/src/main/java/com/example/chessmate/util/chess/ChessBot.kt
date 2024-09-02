package com.example.chessmate.util.chess

import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardEvaluator
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator
import kotlin.system.measureTimeMillis

class ChessBot(val color: PieceColor){
    private val cacheLimit = 1000
    private val cache = object : LinkedHashMap<String, Pair<Int, BitMove?>>(cacheLimit, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, Pair<Int, BitMove?>>?): Boolean {
            return size > cacheLimit
        }
    }

    //the println's and the speed benchmark to be removed later
    fun findBestMove(bitboard: Bitboard, depth: Int, maximizingPlayer: Boolean): BitMove? {
        var move: BitMove?
        val time = measureTimeMillis {
            move = alphaBeta(bitboard, depth, Int.MIN_VALUE, Int.MAX_VALUE, maximizingPlayer, color).second
        }
        println("time: ${time / 1000.0} s")
        return move
    }

    private fun alphaBeta(board: Bitboard, depth: Int, alpha: Int, beta: Int, maximizingPlayer: Boolean, currentColor: PieceColor): Pair<Int, BitMove?> {
        val cacheKey = board.toString() + "_${currentColor}"

        cache[cacheKey]?.let {
            if (it.second != null || depth == 0 || board.isGameEnded()) {
                return it
            }
        }

        if (depth == 0 || board.isGameEnded()) {
            val evaluator = BitboardEvaluator(board)
            val evaluation = Pair(evaluator.evaluate(), null)
            cache[cacheKey] = evaluation
            return evaluation
        }

        val moveGenerator = BitboardMoveGenerator(board)
        val legalMoves = moveGenerator.generateLegalMovesForAlphaBeta(maximizingPlayer)

        val prioritizedMoves = legalMoves.map { move ->
            val decodedMove = BitboardMoveGenerator.decodeMove(move)
            val newBoard = board.copy().apply { movePiece(decodedMove) }
            val evaluator = BitboardEvaluator(newBoard)

            val seeValue = evaluateStaticExchange(board, decodedMove)

            val isCapture = decodedMove.capturedPiece != BitPiece.NONE
            val isCheck = decodedMove.isCheck
            val isPromotion = decodedMove.promotion != BitPiece.NONE

            val priority = when {
                isCheck && seeValue >= 0 -> 3
                isCapture && seeValue >= 0 -> 2
                isPromotion -> 1
                seeValue < 0 -> -1
                else -> 0
            }

            Triple(decodedMove, priority, evaluator.evaluate())
        }.filter { (_, priority, _) ->
            priority >= 0
        }.sortedWith(
            if (maximizingPlayer) {
                compareByDescending<Triple<BitMove, Int, Int>> { it.second }
                    .thenByDescending { it.third } //max player wants high scores
            } else {
                compareByDescending<Triple<BitMove, Int, Int>> { it.second }
                    .thenBy { it.third } //min player wants low scores
            }
        ).map { it.first }


        var localAlpha = alpha
        var localBeta = beta
        var bestMove: BitMove? = null
        var bestValue = if (maximizingPlayer) Int.MIN_VALUE else Int.MAX_VALUE

        for (move in prioritizedMoves.take(3)) {
            val newBoard = board.copy().apply { movePiece(move) }
            val newCacheKey = newBoard.toString() + "_${currentColor.opposite()}"

            val (value, _) = if (cache.containsKey(newCacheKey)) {
                cache[newCacheKey]!!
            } else {
                val result = alphaBeta(
                    newBoard,
                    depth - 1,
                    localAlpha,
                    localBeta,
                    !maximizingPlayer,
                    currentColor.opposite()
                )
                cache[newCacheKey] = result
                result
            }

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value
                    bestMove = move
                }
                localAlpha = maxOf(localAlpha, bestValue)
            } else {
                if (value < bestValue) {
                    bestValue = value
                    bestMove = move
                }
                localBeta = minOf(localBeta, bestValue)
            }

            if (localBeta <= localAlpha) break
        }
        val result = Pair(bestValue, bestMove)
        cache[cacheKey] = result
        return result
    }

    private fun evaluateStaticExchange(board: Bitboard, move: BitMove): Int {
        var gain = move.capturedPiece.value()
        val square = move.to
        val newBoard = board.copy().apply { movePiece(move) }
        var attackerColor = move.piece.color().opposite()
        var sign = -1

        val gainSequence = mutableListOf<Int>()
        gainSequence.add(gain)

        while (true) {
            val attackingMoves = BitboardMoveGenerator(newBoard)
                .generateLegalMovesForAlphaBeta(attackerColor == PieceColor.WHITE)
                .map { BitboardMoveGenerator.decodeMove(it) }
                .filter { it.to == square }
                .sortedBy { it.piece.value() }

            if (attackingMoves.isEmpty()) break

            val bestAttackingMove = attackingMoves.first()
            val attackerValue = bestAttackingMove.piece.value()

            gain += sign * attackerValue
            gainSequence.add(gain)

            newBoard.movePiece(bestAttackingMove)
            attackerColor = attackerColor.opposite()
            sign *= -1
        }

        var bestOutcome = gainSequence.last()
        for (i in gainSequence.size - 2 downTo 0) {
            bestOutcome = -maxOf(gainSequence[i], bestOutcome)
        }

        return bestOutcome
    }
}