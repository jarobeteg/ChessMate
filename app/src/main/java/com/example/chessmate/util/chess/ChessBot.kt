package com.example.chessmate.util.chess

class ChessBot(val botColor: PieceColor, private val depth: Int){
    var isBotTurn: Boolean = false

    fun getBestMove(chessboard: Chessboard): Move?{
        //before generating moves you need to check if the chessbot king is in check
        var isBotInCheck: Boolean
        if (botColor == PieceColor.BLACK){
            isBotInCheck = chessboard.isKingInCheck(chessboard, chessboard.getBlackKingSquare()!!, botColor)
        } else {
            isBotInCheck = chessboard.isKingInCheck(chessboard, chessboard.getWhiteKingSquare()!!, botColor)
        }
        if (isBotInCheck){
            println("The bot is in check")
        } else {
            println("The bot is not in check")
        }
        return findBestMoves(chessboard)
    }

    private fun findBestMoves(chessboard: Chessboard): Move?{
        val botMoveGenerator = LegalMoveGenerator()
        val legalMoves = botMoveGenerator.generateLegalMovesForBot(chessboard, botColor)
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