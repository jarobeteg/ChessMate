package com.example.chessmate.util.chess

class ChessboardEvaluator(){

    private val pieceValues = mapOf(
        PieceType.PAWN to 1.0F,
        PieceType.KNIGHT to 3.0F,
        PieceType.BISHOP to 3.0F,
        PieceType.ROOK to 5.0F,
        PieceType.QUEEN to 9.0F
    )

    fun evaluatePosition(chessboard: Chessboard, legalMoveGenerator: LegalMoveGenerator, botColor: PieceColor): Float{
        var positionScore = 0.00F

        positionScore += materialBalance(chessboard)

        positionScore += pieceMobility(chessboard, legalMoveGenerator, botColor)

        return positionScore
    }

    private fun materialBalance(chessboard: Chessboard): Float{
        var materialBalanceScore = 0.0F

        val whitePieces = chessboard.getAllPieces(PieceColor.WHITE)
        val blackPieces = chessboard.getAllPieces(PieceColor.BLACK)

        for ((pieceType, value) in pieceValues){
            val whiteCount = whitePieces.count { it == pieceType }
            val blackCount = blackPieces.count { it == pieceType }

            materialBalanceScore += (whiteCount - blackCount) * value
        }

        return materialBalanceScore
    }

    private fun pieceMobility(chessboard: Chessboard, legalMoveGenerator: LegalMoveGenerator, botColor: PieceColor): Float{
        var pieceMobilityScore = 0.0F

        val whiteMoves = legalMoveGenerator.generateLegalMoves(chessboard, PieceColor.WHITE, botColor == PieceColor.WHITE)
        val blackMoves = legalMoveGenerator.generateLegalMoves(chessboard, PieceColor.BLACK, botColor == PieceColor.BLACK)

        pieceMobilityScore += whiteMoves.count()
        pieceMobilityScore -= blackMoves.count()

        return pieceMobilityScore
    }

    private fun kingSafety(): Float{
        var kingSafetyScore = 0.0F

        return kingSafetyScore
    }
    private fun centerControl(): Float{
        var centerControlScore = 0.0F

        return centerControlScore
    }
    private fun pieceDevelopment(): Float{
        var pieceDevelopmentScore = 0.0F

        return pieceDevelopmentScore
    }
    private fun pawnPromotion(): Float{
        var pawnPromotionScore = 0.0F

        return pawnPromotionScore
    }
    private fun pawnChains(): Float{
        var pawnChainsScore = 0.0F

        return pawnChainsScore
    }
    private fun kingActivity(): Float{
        var kingActivityScore = 0.0F

        return kingActivityScore
    }
    private fun tacticalOpportunities(): Float{
        var tacticalOpportunitiesScore = 0.0F

        return tacticalOpportunitiesScore
    }
    private fun tempo(): Float{
        var tempoScore = 0.0F

        return tempoScore
    }

}