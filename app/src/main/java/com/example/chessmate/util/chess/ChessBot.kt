package com.example.chessmate.util.chess

class ChessBot(private val botColor: PieceColor, private val depth: Int, private var chessboard: Chessboard){

    fun getBestMove(): Move?{
        //before generating moves you need to check if the chessbot king is in check
        return findBestMoves()
    }

    private fun findBestMoves(): Move?{
        val botMoveGenerator = LegalMoveGenerator(chessboard, botColor)
        val legalMoves = botMoveGenerator.generateLegalMovesForBot()
        if (botColor == PieceColor.WHITE){
            return bestMoveAsWhite(legalMoves)
        } else {
            return bestMoveAsBlack(legalMoves)
        }
    }

    private fun bestMoveAsBlack(legalMoves: List<Move>): Move?{
        var tmpScore: Int = Int.MAX_VALUE
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
        var tmpScore: Int = Int.MIN_VALUE
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