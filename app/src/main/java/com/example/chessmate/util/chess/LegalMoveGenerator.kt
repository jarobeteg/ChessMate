package com.example.chessmate.util.chess

import android.system.Os

class LegalMoveGenerator(){

    fun generateLegalMoves(chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = chessboard.getSquare(row, col)
                if (piece.pieceColor == pieceColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(chessboard, row, col, pieceColor, isForBot))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(chessboard, row, col, pieceColor, isForBot))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(chessboard, row, col, pieceColor, isForBot))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(chessboard, row, col, pieceColor, isForBot))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(chessboard, row, col, pieceColor, isForBot))
                        PieceType.KING -> legalMoves.addAll(generateKingMoves(chessboard, row, col, pieceColor, isForBot))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    fun canMoveTo(chessboard: Chessboard, square: Square, position: Position, isForBot: Boolean): Boolean{
        when (square.pieceType) {
            PieceType.PAWN -> {
                val legalMoves = generatePawnMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.KNIGHT -> {
                val legalMoves = generateKnightMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.BISHOP -> {
                val legalMoves = generateBishopMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.ROOK -> {
                val legalMoves = generateRookMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.QUEEN -> {
                val legalMoves = generateQueenMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.KING -> {
                val legalMoves = generateKingMoves(chessboard, square.row, square.col, square.pieceColor, isForBot)
                for (move in legalMoves) {
                    if (move.destinationPosition.row == position.row && move.destinationPosition.col == position.col) {
                        return true
                    }
                }
            }
            PieceType.NONE -> {}
        }

        return false
    }

    fun generatePawnMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        val direction = if (isForBot) 1 else -1

        addRegularPawnMove(chessboard, row, col, direction, pieceColor, legalMoves, isForBot)

        addDoublePawnMove(chessboard, row, col, direction, pieceColor, legalMoves, isForBot)

        addDiagonalPawnMoves(chessboard, row, col, direction, pieceColor, legalMoves, isForBot)

        addEnPassantMoves(chessboard, row, col, direction, pieceColor, legalMoves, isForBot)

        addPawnPromotionMoves(chessboard, row, col, direction, pieceColor, legalMoves, isForBot)

        return legalMoves
    }

    private fun addRegularPawnMove(
        chessboard: Chessboard,
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
            val sourcePieceColor = sourceSquare.pieceColor
            val sourcePieceType = sourceSquare.pieceType
            val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
            val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
            val regularMove = RegularMove(sourcePosition, destinationPosition, sourcePieceColor, sourcePieceType)
            applyMoveAndCheckForCheck(chessboard, regularMove, pieceColor, legalMoves)
        }
    }

    private fun addDoublePawnMove(
        chessboard: Chessboard,
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
                val sourcePieceColor = sourceSquare.pieceColor
                val sourcePieceType = sourceSquare.pieceType
                val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
                val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
                val regularMove = RegularMove(sourcePosition, destinationPosition, sourcePieceColor, sourcePieceType)
                applyMoveAndCheckForCheck(chessboard, regularMove, pieceColor, legalMoves)
            }
        }
    }

    private fun addDiagonalPawnMoves(
        chessboard: Chessboard,
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

        addDiagonalPawnMove(chessboard, row, col, newRow, leftCol, pieceColor, legalMoves, isForBot)
        addDiagonalPawnMove(chessboard, row, col, newRow, rightCol, pieceColor, legalMoves, isForBot)
    }

    private fun addDiagonalPawnMove(
        chessboard: Chessboard,
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
                val sourcePieceColor = sourceSquare.pieceColor
                val sourcePieceType = sourceSquare.pieceType
                val opponentPieceColor = destinationSquare.pieceColor
                val opponentPieceType = destinationSquare.pieceType
                val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
                val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
                val moveAndCapture = MoveAndCapture(sourcePosition, destinationPosition, sourcePieceColor, sourcePieceType, opponentPieceColor, opponentPieceType)
                applyMoveAndCheckForCheck(chessboard, moveAndCapture, pieceColor, legalMoves)
            }
        }
    }

    private fun addEnPassantMoves(
        chessboard: Chessboard,
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
        chessboard: Chessboard,
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
                val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
                val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
                val pawnPromotionMove = PawnPromotionMove(sourcePosition, destinationPosition, pieceColor, pieceType)
                applyMoveAndCheckForCheck(chessboard, pawnPromotionMove, pieceColor, legalMoves)
            }

            addDiagonalPromotionMoves(chessboard, row, col, direction, pieceColor, pieceType, legalMoves, isForBot)
        }
    }

    private fun addDiagonalPromotionMoves(
        chessboard: Chessboard,
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

        addDiagonalPromotionMove(chessboard, row, col, newRow, leftCol, pieceColor, pieceType, legalMoves, isForBot)
        addDiagonalPromotionMove(chessboard, row, col, newRow, rightCol, pieceColor, pieceType, legalMoves, isForBot)
    }

    private fun addDiagonalPromotionMove(
        chessboard: Chessboard,
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
                val capturedPieceColor = destinationSquare.pieceColor
                val capturedPieceType = destinationSquare.pieceType
                val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
                val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
                val pawnPromotionCaptureMove = PawnPromotionCaptureMove(
                    sourcePosition,
                    destinationPosition,
                    pieceColor,
                    pieceType,
                    capturedPieceColor,
                    capturedPieceType
                )
                applyMoveAndCheckForCheck(chessboard, pawnPromotionCaptureMove, pieceColor, legalMoves)
            }
        }
    }

    fun generateKnightMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false):  MutableList<Move>{
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
                    generateRegularMove(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                }
            }
        }

        return legalMoves
    }

    fun generateBishopMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
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
                    generateRegularMove(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                   generateMoveAndCapture(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
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

    fun generateRookMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
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
                    generateRegularMove(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
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

    fun generateQueenMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
        val legalMoves = mutableListOf<Move>()

        legalMoves.addAll(generateBishopMoves(chessboard, row, col, pieceColor, isForBot))
        legalMoves.addAll(generateRookMoves(chessboard, row, col, pieceColor, isForBot))

        return legalMoves
    }

    fun generateKingMoves(chessboard: Chessboard, row: Int, col: Int, pieceColor: PieceColor, isForBot: Boolean = false): MutableList<Move>{
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
                    generateRegularMove(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                    if (!chessboard.isKingInCheck(kingSquare, pieceColor) && !kingSquare.hasMoved){
                        if (isKingSideCastlePossible(chessboard, isPlayerStarted, isForBot)){
                            generateCastleMove(chessboard, isPlayerStarted, isForBot, true, pieceColor, legalMoves)
                        }

                        if (isQueenSideCastlePossible(chessboard, isPlayerStarted, isForBot)){
                            generateCastleMove(chessboard, isPlayerStarted, isForBot, false, pieceColor, legalMoves)
                        }
                    }
                } else if (chessboard.isOpponentPiece(newRow, newCol, pieceColor) && !chessboard.isOpponentKingSquare(newRow, newCol, pieceColor)){
                    generateMoveAndCapture(chessboard, sourceSquare, destinationSquare, pieceColor, legalMoves)
                }
            }
        }

        return legalMoves
    }

    private fun isKingSideCastlePossible(chessboard: Chessboard, isPlayerStarted: Boolean, isForBot: Boolean): Boolean{
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
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor)){
                    return false
                }
            }
        } else {
            for (i in 1 until 3){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col - (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor)){
                    return false
                }
            }
        }

        return true
    }

    private fun isQueenSideCastlePossible(chessboard: Chessboard, isPlayerStarted: Boolean, isForBot: Boolean): Boolean{
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
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor)){
                    return false
                }
            }
        } else {
            for (i in 1 until 4){
                val updatedKingSquare = chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i))
                if (chessboard.getSquare(kingSquare.row, kingSquare.col + (1 * i)).isOccupied){
                    return false
                }
                if (chessboard.isKingInCheck(updatedKingSquare, kingSquare.pieceColor)){
                    return false
                }
            }
        }

        return true
    }

    private fun generateRegularMove(
        chessboard: Chessboard,
        sourceSquare: Square,
        destinationSquare: Square,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        val sourcePieceColor = sourceSquare.pieceColor
        val sourcePieceType = sourceSquare.pieceType
        val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
        val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
        val regularMove = RegularMove(sourcePosition, destinationPosition, sourcePieceColor, sourcePieceType)
        applyMoveAndCheckForCheck(chessboard, regularMove, pieceColor, legalMoves)
    }

    private fun generateMoveAndCapture(
        chessboard: Chessboard,
        sourceSquare: Square,
        destinationSquare: Square,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        val sourcePieceColor = sourceSquare.pieceColor
        val sourcePieceType = sourceSquare.pieceType
        val opponentPieceColor = destinationSquare.pieceColor
        val opponentPieceType = destinationSquare.pieceType
        val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
        val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
        val moveAndCapture = MoveAndCapture(sourcePosition, destinationPosition, sourcePieceColor, sourcePieceType, opponentPieceColor, opponentPieceType)
        applyMoveAndCheckForCheck(chessboard, moveAndCapture, pieceColor, legalMoves)
    }

    private fun generateCastleMove(
        chessboard: Chessboard,
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
        val sourcePieceColor = sourceSquare.pieceColor
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
        val sourcePosition = Position(sourceSquare.row, sourceSquare.col)
        val destinationPosition = Position(destinationSquare.row, destinationSquare.col)
        val rookSourcePosition = Position(rookSourceSquare.row, rookSourceSquare.col)
        val rookDestinationPosition = Position(rookDestinationSquare.row, rookDestinationSquare.col)
        val castleMove = CastleMove(sourcePosition, destinationPosition, sourcePieceColor, rookSourcePosition, rookDestinationPosition, isKingSideCastles)
        applyMoveAndCheckForCheck(chessboard, castleMove, pieceColor, legalMoves)
    }

    private fun applyMoveAndCheckForCheck(
        chessboard: Chessboard,
        move: Move,
        pieceColor: PieceColor,
        legalMoves: MutableList<Move>
    ) {
        chessboard.performMove(move)
        val kingSquare = chessboard.getKingSquare(pieceColor)

        if (!chessboard.isKingInCheck(kingSquare, pieceColor)) {
            legalMoves.add(move)
        }

        chessboard.reverseMove(move)
    }
}