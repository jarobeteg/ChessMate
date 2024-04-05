package com.example.chessmate.util.chess

class ChessboardEvaluator(private val chessboard: Chessboard){

    private val pieceValues = mapOf(
        PieceType.PAWN to 1.0F,
        PieceType.KNIGHT to 3.0F,
        PieceType.BISHOP to 3.0F,
        PieceType.ROOK to 5.0F,
        PieceType.QUEEN to 9.0F
    )

    fun evaluatePosition(): Float{
        var positionScore = 0.00F

        positionScore += materialBalance()

        return positionScore
    }

    private fun materialBalance(): Float{
        var materialBalance = 0.0F

        val whitePieces = chessboard.getAllPieces(PieceColor.WHITE)
        val blackPieces = chessboard.getAllPieces(PieceColor.BLACK)

        for ((pieceType, value) in pieceValues){
            val whiteCount = whitePieces.count { it == pieceType }
            val blackCount = blackPieces.count { it == pieceType }

            materialBalance += (whiteCount - blackCount) * value
        }

        return materialBalance
    }

    private fun pieceMobility(): Float{
        return 0.00F
    }

    private fun kingSafety(): Float{
        return 0.00F
    }
    private fun centerControl(): Float{
        return 0.00F
    }
    private fun pieceDevelopment(): Float{
        return 0.00F
    }
    private fun pawnPromotion(): Float{
        return 0.00F
    }
    private fun pawnChains(): Float{
        return 0.00F
    }
    private fun kingActivity(): Float{
        return 0.00F
    }
    private fun tacticalOpportunities(): Float{
        return 0.00F
    }
    private fun tempo(): Float{
        return 0.00F
    }

}