package com.example.chessmate.util.chess

class ChessboardEvaluator(){

    private val pst = PieceSquareTables()

    private val pieceValues = mapOf(
        PieceType.PAWN to 1.0F,
        PieceType.KNIGHT to 3.0F,
        PieceType.BISHOP to 3.0F,
        PieceType.ROOK to 5.0F,
        PieceType.QUEEN to 9.0F
    )

    fun evaluatePosition(chessboard: Chessboard, legalMoveGenerator: LegalMoveGenerator, playerColor: PieceColor, botColor: PieceColor): Float{
        var positionScore = 0.00F

        positionScore += materialBalance(chessboard)

        positionScore += pieceMobility(chessboard, legalMoveGenerator, botColor)

        positionScore += kingSafety(chessboard)

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

    private fun kingSafety(chessboard: Chessboard): Float{
        var kingsSafetyScore = 0.0F

        val whiteKingSquare = chessboard.getKingSquare(PieceColor.WHITE)
        val blackKingSquare = chessboard.getKingSquare(PieceColor.BLACK)

        val whiteKingSafety = evaluateKingSafety(chessboard, whiteKingSquare, PieceColor.WHITE)
        val blackKingSafety = evaluateKingSafety(chessboard, blackKingSquare, PieceColor.BLACK)

        kingsSafetyScore += whiteKingSafety
        kingsSafetyScore -= blackKingSafety

        return kingsSafetyScore
    }

    private fun evaluateKingSafety(chessboard: Chessboard, kingSquare: Square, kingColor: PieceColor): Float {
        var safetyScore = 0.0F

        val surroundingSquares = getSurroundingPositions(chessboard, Position(kingSquare.row, kingSquare.col))

        surroundingSquares.forEach { position ->
            val square = chessboard.getSquare(position.row, position.col)
            if (square.isOccupied) {
                if (square.pieceColor == kingColor) {
                    safetyScore += getProtectionScore(square.pieceType)
                } else {
                    safetyScore -= getThreatScore(square.pieceType)
                }
            }
        }

        safetyScore += evaluatePawnShield(chessboard, kingSquare, kingColor)
        safetyScore -= evaluateOpenFiles(chessboard, kingSquare)

        return safetyScore
    }

    private fun getSurroundingPositions(chessboard: Chessboard, kingPosition: Position): List<Position> {
        val directions = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        return directions.map { (dRow, dCol) ->
            Position(kingPosition.row + dRow, kingPosition.col + dCol)
        }.filter { position -> chessboard.isValidPosition(position) }
    }

    private fun getProtectionScore(pieceType: PieceType): Float {
        return when (pieceType) {
            PieceType.PAWN -> 0.5F
            PieceType.KNIGHT, PieceType.BISHOP -> 0.3F
            PieceType.ROOK -> 0.2F
            PieceType.QUEEN -> 0.1F
            PieceType.NONE -> 0.0F
            else -> 0.0F
        }
    }

    private fun getThreatScore(pieceType: PieceType): Float {
        return when (pieceType) {
            PieceType.PAWN -> 0.5F
            PieceType.KNIGHT, PieceType.BISHOP -> 0.7F
            PieceType.ROOK -> 1.0F
            PieceType.QUEEN -> 1.5F
            PieceType.NONE -> 0.0F
            else -> 0.0F
        }
    }

    private fun evaluatePawnShield(chessboard: Chessboard, kingSquare: Square, kingColor: PieceColor): Float {
        var pawnShieldScore = 0.0F
        val row = kingSquare.row
        val col = kingSquare.col

        val positionToCheck = listOf(
            Position(row - 1, col - 1), Position(row - 1, col), Position(row - 1, col + 1),
            Position(row, col - 1), Position(row, col + 1),
            Position(row + 1, col - 1), Position(row + 1, col), Position(row + 1, col + 1)
        )

        positionToCheck.forEach { position ->
            if (chessboard.isValidPosition(position)) {
                val square = chessboard.getSquare(position.row, position.col)
                if (square.isOccupied && square.pieceColor == kingColor && square.pieceType == PieceType.PAWN){
                    pawnShieldScore += 0.5F
                }
            }
        }

        return pawnShieldScore
    }

    private fun evaluateOpenFiles(chessboard: Chessboard, kingSquare: Square): Float {
        var openFilesScore = 0.0F
        val col = kingSquare.col

        val isFileOpen = (0..7).all { row ->
            val square = chessboard.getSquare(row, col)
            !square.isOccupied || square.pieceType != PieceType.PAWN
        }

        val isFileSemiOpen = (0..7).all { row ->
            val square = chessboard.getSquare(row, col)
            (square.isOccupied && square.pieceColor != kingSquare.pieceColor && square.pieceType == PieceType.PAWN)
        }

        if (isFileOpen) {
            openFilesScore = 1.0F
        } else if (isFileSemiOpen) {
            openFilesScore = 0.5F
        }

        return openFilesScore
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