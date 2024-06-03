package com.example.chessmate.util.chess

class ChessBot(private val chessboard: Chessboard, private val chessboardEvaluator: ChessboardEvaluator,
               private val legalMoveGenerator: LegalMoveGenerator, val botColor: PieceColor, private val depth: Int){
    var isBotTurn: Boolean = false

    fun getBestMove(): Move?{
        //before generating moves you need to check if the chessbot king is in check
        return findBestMoves()
    }

    private fun findBestMoves(): Move?{
        val legalMoves = legalMoveGenerator.generateLegalMoves(chessboard, botColor, true)
        if (legalMoves.isEmpty()){
            println("Bot checkmated")
        }
        if (botColor == PieceColor.WHITE){
            return bestMoveAsWhite(legalMoves)
        } else {
            return bestMoveAsBlack(legalMoves)
        }
    }

    private fun bestMoveAsBlack(legalMoves: List<Move>): Move?{
        var tmpScore: Float = Float.MAX_VALUE
        var bestMove: Move? = null
        for (move in legalMoves){
            if (move.score < tmpScore){
                tmpScore = move.score
            }
        }
        for (move in legalMoves){
            if (tmpScore == move.score){
                bestMove = move
            }
        }

        return bestMove
    }

    private fun bestMoveAsWhite(legalMoves: List<Move>): Move?{
        var tmpScore: Float = Float.MIN_VALUE
        var bestMove: Move? = null
        for (move in legalMoves){
            if (move.score > tmpScore){
                tmpScore = move.score
            }
        }
        for (move in legalMoves){
            if (tmpScore == move.score){
                bestMove = move
            }
        }

        return bestMove
    }
}