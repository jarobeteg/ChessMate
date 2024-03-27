package com.example.chessmate.util.chess

class LegalMoveGenerator(private val chessboard: Chessboard){
    private val chessboardEvaluator = ChessboardEvaluator(chessboard)

    fun generateLegalMoves(pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = chessboard.getSquare(row, col)
                if (piece.pieceColor == pieceColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(row, col, pieceColor, isForBot))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(row, col, pieceColor, isForBot))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(row, col, pieceColor, isForBot))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(row, col, pieceColor, isForBot))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(row, col, pieceColor, isForBot))
                        PieceType.KING -> legalMoves.addAll(generateKingMoves(row, col, pieceColor, isForBot))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    fun generatePawnMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        val direction = if (isForBot) 1 else -1

        addRegularPawnMove(row, col, direction, pieceColor, legalMoves, isForBot)

        addDoublePawnMove(row, col, direction, pieceColor, legalMoves, isForBot)

        addDiagonalPawnMoves(row, col, direction, pieceColor, legalMoves, isForBot)

        addEnPassantMoves(row, col, direction, pieceColor, legalMoves, isForBot)

        addPawnPromotionMoves(row, col, direction, pieceColor, legalMoves, isForBot)

        return legalMoves
    }

    private fun addRegularPawnMove(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        val newRow = row + direction
        if (chessboard.isValidSquare(newRow, col)
            && chessboard.isEmptySquare(newRow, col)
            && newRow != 0 && newRow != 7) {
            val sourceSquare = chessboard.getSquare(row, col)
            val destinationSquare = chessboard.getSquare(newRow, col)
            val sourcePieceColor = sourceSquare.pieceColor!!
            val sourcePieceType = sourceSquare.pieceType!!
            val regularMove = RegularMove(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType)
            applyMoveAndCheckForCheck(regularMove, pieceColor, legalMoves)
        }
    }

    private fun addDoublePawnMove(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        val newRow = row + direction
        if ((row == 1 && isForBot) || (row == 6 && !isForBot)) {
            val nextRow = newRow + direction
            if (chessboard.isEmptySquare(newRow, col) && chessboard.isEmptySquare(nextRow, col)) {
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(nextRow, col)
                val sourcePieceColor = sourceSquare.pieceColor!!
                val sourcePieceType = sourceSquare.pieceType!!
                val regularMove = RegularMove(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType)
                applyMoveAndCheckForCheck(regularMove, pieceColor, legalMoves)
            }
        }
    }

    private fun addDiagonalPawnMoves(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        val leftCol = col - 1
        val rightCol = col + 1
        val newRow = row + direction

        addDiagonalPawnMove(row, col, newRow, leftCol, pieceColor, legalMoves, isForBot)
        addDiagonalPawnMove(row, col, newRow, rightCol, pieceColor, legalMoves, isForBot)
    }

    private fun addDiagonalPawnMove(
        row: Int,
        col: Int,
        newRow: Int,
        newCol: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        if (chessboard.isValidSquare(newRow, newCol) && newRow != 0 && newRow != 7) {
            val sourceSquare = chessboard.getSquare(row, col)
            val destinationSquare = chessboard.getSquare(newRow, newCol)
            if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)) {
                val sourcePieceColor = sourceSquare.pieceColor!!
                val sourcePieceType = sourceSquare.pieceType!!
                val opponentPieceColor = destinationSquare.pieceColor!!
                val opponentPieceType = destinationSquare.pieceType!!
                val moveAndCapture = MoveAndCapture(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType, opponentPieceColor, opponentPieceType)
                applyMoveAndCheckForCheck(moveAndCapture, pieceColor, legalMoves)
            }
        }
    }

    private fun addEnPassantMoves(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        //TODO: Implement en passant logic
    }

    private fun addPawnPromotionMoves(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        val promotionPieceTypes = arrayOf(PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP)
        val newRow = row + direction
        for (pieceType in promotionPieceTypes) {
            if (chessboard.isValidSquare(newRow, col)
                && chessboard.isEmptySquare(newRow, col)
                && ((row == 1 && !isForBot) || (row == 6 && isForBot))) {
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(row + direction, col)
                val pawnPromotionMove = PawnPromotionMove(sourceSquare, destinationSquare, pieceColor, pieceType)
                applyMoveAndCheckForCheck(pawnPromotionMove, pieceColor, legalMoves)
            }

            addDiagonalPromotionMoves(row, col, direction, pieceColor, pieceType, legalMoves, isForBot)
        }
    }

    private fun addDiagonalPromotionMoves(
        row: Int,
        col: Int,
        direction: Int,
        pieceColor: PieceColor,
        pieceType: PieceType,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        val leftCol = col - 1
        val rightCol = col + 1
        val newRow = row + direction

        addDiagonalPromotionMove(row, col, newRow, leftCol, pieceColor, pieceType, legalMoves, isForBot)
        addDiagonalPromotionMove(row, col, newRow, rightCol, pieceColor, pieceType, legalMoves, isForBot)
    }

    private fun addDiagonalPromotionMove(
        row: Int,
        col: Int,
        newRow: Int,
        newCol: Int,
        pieceColor: PieceColor,
        pieceType: PieceType,
        legalMoves: MutableList<Move>,
        isForBot: Boolean
    ) {
        if (chessboard.isValidSquare(newRow, newCol) && ((row == 1 && !isForBot) || (row == 6 && isForBot))) {
            val sourceSquare = chessboard.getSquare(row, col)
            val destinationSquare = chessboard.getSquare(newRow, newCol)
            if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)) {
                val capturedPieceColor = destinationSquare.pieceColor!!
                val capturedPieceType = destinationSquare.pieceType!!
                val pawnPromotionCaptureMove = PawnPromotionCaptureMove(
                    sourceSquare,
                    destinationSquare,
                    pieceColor,
                    pieceType,
                    capturedPieceColor,
                    capturedPieceType
                )
                applyMoveAndCheckForCheck(pawnPromotionCaptureMove, pieceColor, legalMoves)
            }
        }
    }

    fun generateKnightMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false):  MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        val moves = arrayOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )

        for ((rowOffset, colOffset) in moves){
            val newRow = row + rowOffset
            val newCol = col + colOffset

            if (chessboard.isValidSquare(newRow, newCol)){
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(newRow, newCol)

                if (chessboard.isEmptySquare(newRow, newCol)){
                    generateRegularMove(sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(sourceSquare, destinationSquare, pieceColor, legalMoves)
                }
            }
        }

        return legalMoves
    }

    fun generateBishopMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (chessboard.isValidSquare(newRow, newCol)){
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(newRow, newCol)

                if (chessboard.isEmptySquare(newRow, newCol)){
                    generateRegularMove(sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                   generateMoveAndCapture(sourceSquare, destinationSquare, pieceColor, legalMoves)
                    break
                } else {
                    break
                }

                newRow += rowOffset
                newCol += colOffset
            }
        }

        return legalMoves
    }

    fun generateRookMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        val directions = arrayOf(
            Pair(-1, 0), Pair(1, 0),
            Pair(0, -1), Pair(0, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (chessboard.isValidSquare(newRow, newCol)){
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(newRow, newCol)

                if (chessboard.isEmptySquare(newRow, newCol)){
                    generateRegularMove(sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(sourceSquare, destinationSquare, pieceColor, legalMoves)
                    break
                } else {
                    break
                }

                newRow += rowOffset
                newCol += colOffset
            }
        }

        return legalMoves
    }

    fun generateQueenMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        legalMoves.addAll(generateBishopMoves(row, col, pieceColor, isForBot))
        legalMoves.addAll(generateRookMoves(row, col, pieceColor, isForBot))

        return legalMoves
    }

    fun generateKingMoves(row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()
        val kingSquare = chessboard.getKingSquare(pieceColor)
        val isPlayerStarted = (!isForBot && pieceColor == PieceColor.WHITE) || (isForBot && pieceColor == PieceColor.BLACK)

        val moves = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in moves){
            val newRow = row + rowOffset
            val newCol = col + colOffset

            if (chessboard.isValidSquare(newRow, newCol)){
                val sourceSquare = chessboard.getSquare(row, col)
                val destinationSquare = chessboard.getSquare(newRow, newCol)

                if (chessboard.isEmptySquare(newRow, newCol)){
                    generateRegularMove(sourceSquare, destinationSquare, pieceColor, legalMoves)
                    if (!chessboard.isKingInCheck(kingSquare, pieceColor) && !kingSquare.hasMoved){
                        if (isKingSideCastlePossible(isPlayerStarted, isForBot)){
                            generateCastleMove(isPlayerStarted, isForBot, true, pieceColor, legalMoves)
                        }

                        if (isQueenSideCastlePossible(isPlayerStarted, isForBot)){
                            generateCastleMove(isPlayerStarted, isForBot, false, pieceColor, legalMoves)
                        }
                    }
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(sourceSquare, destinationSquare, pieceColor, legalMoves)
                }
            }
        }

        return legalMoves
    }

    private fun isKingSideCastlePossible(isPlayerStarted: Boolean, isForBot: Boolean): Boolean{
        val kingSquare = chessboard.getStartingKingSquare(isPlayerStarted, isForBot)
        val rookSquare = chessboard.getStartingKingSideRookSquare(isPlayerStarted, isForBot)

        if (kingSquare.hasMoved || rookSquare.hasMoved){
            return false
        }

        if (isPlayerStarted){
            for (i in 1 until 3){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor!!)){
                    return false
                }
            }
        } else {
            for (i in 1 until 3){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor!!)){
                    return false
                }
            }
        }

        return true
    }

    private fun isQueenSideCastlePossible(isPlayerStarted: Boolean, isForBot: Boolean): Boolean{
        val kingSquare = chessboard.getStartingKingSquare(isPlayerStarted, isForBot)
        val rookSquare = chessboard.getStartingQueenSideRookSquare(isPlayerStarted, isForBot)

        if (kingSquare.hasMoved || rookSquare.hasMoved){
            return false
        }

        if (isPlayerStarted){
            for (i in 1 until 4){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor!!)){
                    return false
                }
            }
        } else {
            for (i in 1 until 4){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor!!)){
                    return false
                }
            }
        }

        return true
    }

    private fun generateRegularMove(
        sourceSquare: Square,
        destinationSquare: Square,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        val sourcePieceColor = sourceSquare.pieceColor!!
        val sourcePieceType = sourceSquare.pieceType!!
        val regularMove = RegularMove(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType)
        applyMoveAndCheckForCheck(regularMove, pieceColor, legalMoves)
    }

    private fun generateMoveAndCapture(
        sourceSquare: Square,
        destinationSquare: Square,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        val sourcePieceColor = sourceSquare.pieceColor!!
        val sourcePieceType = sourceSquare.pieceType!!
        val opponentPieceColor = destinationSquare.pieceColor!!
        val opponentPieceType = destinationSquare.pieceType!!

        val moveAndCapture = MoveAndCapture(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType, opponentPieceColor, opponentPieceType)
        applyMoveAndCheckForCheck(moveAndCapture, pieceColor, legalMoves)
    }

    private fun generateCastleMove(
        isPlayerStarted: Boolean,
        isForBot: Boolean,
        isKingSideCastles: Boolean,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ){
        val sourceSquare = chessboard.getStartingKingSquare(isPlayerStarted, isForBot)
        val direction = if (isPlayerStarted) 1 else -1
        val kingDirection = 2 * direction
        val rookDirection = if (isKingSideCastles) 2 else 3
        val sourcePieceColor = sourceSquare.pieceColor!!
        lateinit var destinationSquare: Square
        lateinit var rookSourceSquare: Square
        lateinit var rookDestinationSquare: Square

        if (isKingSideCastles){
            destinationSquare = chessboard.getSquare(sourceSquare.row, sourceSquare.col + kingDirection)
            rookSourceSquare = chessboard.getStartingKingSideRookSquare(isPlayerStarted, isForBot)
            rookDestinationSquare = chessboard.getSquare(rookSourceSquare.row, rookSourceSquare.col - (rookDirection * direction))
        } else {
            destinationSquare = chessboard.getSquare(sourceSquare.row, sourceSquare.col - kingDirection)
            rookSourceSquare = chessboard.getStartingQueenSideRookSquare(isPlayerStarted, isForBot)
            rookDestinationSquare = chessboard.getSquare(rookSourceSquare.row, rookSourceSquare.col + (rookDirection * direction))
        }

        val castleMove = CastleMove(sourceSquare, destinationSquare, sourcePieceColor, rookSourceSquare, rookDestinationSquare, isKingSideCastles)
        applyMoveAndCheckForCheck(castleMove, pieceColor, legalMoves)
    }

    private fun applyMoveAndCheckForCheck(
        move: Move,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        chessboard.performMove(move)
        val kingSquare = chessboard.getKingSquare(pieceColor)

        if (!chessboard.isKingInCheck(kingSquare, pieceColor)) {
            move.score = chessboardEvaluator.evaluatePosition()
            legalMoves.add(move)
        }

        chessboard.reverseMove(move)
    }
}