package com.example.chessmate.util.chess

class Chessboard{
    private val board: Array<Array<Square>> = Array(8) { Array(8) { Square(0, 0, false,  PieceColor.NONE, PieceType.NONE) } }

    init {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                board[row][col] = Square(row, col, false, PieceColor.NONE, PieceType.NONE)
            }
        }
    }

    fun cloneBoardWithoutUI(): Chessboard{
        val clonedBoard = Chessboard()

        for (row in 0 until 8){
            for (col in 0 until 8){
                clonedBoard.board[row][col] = board[row][col].copyWithoutUI()
            }
        }

        return clonedBoard
    }

    fun placePiece(row: Int, col: Int, pieceColor: PieceColor, pieceType: PieceType) {
        val square: Square = getSquare(row, col)
        square.isOccupied = true
        square.pieceColor = pieceColor
        square.pieceType = pieceType
    }

    fun getSquare(row: Int, col: Int): Square {
        return board[row][col]
    }

    fun getSquare(position: Position): Square {
        return board[position.row][position.col]
    }

    fun getSquares(): Array<Array<Square>> {
        return board
    }

    fun isValidSquare(row: Int, col: Int): Boolean{
        return row in 0 until 8 && col in 0 until 8
    }

    fun isValidPosition(position: Position): Boolean {
        return position.row in 0 until 8 && position.col in 0 until 8
    }

    fun isEmptySquare(row: Int, col: Int): Boolean{
        return getSquare(row, col).pieceType == PieceType.NONE
    }

    fun isOpponentPiece(row: Int, col: Int, pieceColor: PieceColor): Boolean{
        if (isEmptySquare(row, col)){
            return false
        }
        return getSquare(row, col).pieceColor != pieceColor
    }

    fun isOpponentKingSquare(row: Int, col: Int, pieceColor: PieceColor): Boolean{
        if (isEmptySquare(row, col)){
            return false
        }
        return getSquare(row, col).pieceType == PieceType.KING && getSquare(row, col).pieceColor != pieceColor
    }

    fun getKingSquare(color: PieceColor): Square{
        return if (color == PieceColor.BLACK) getBlackKingSquare() else getWhiteKingSquare()
    }

    private fun getWhiteKingSquare(): Square{
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceColor == PieceColor.WHITE && square.pieceType == PieceType.KING) {
                    return square
                }
            }
        }
        throw IllegalStateException("White king not found on the board")
    }

    private fun getBlackKingSquare(): Square{
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceColor == PieceColor.BLACK && square.pieceType == PieceType.KING) {
                    return square
                }
            }
        }
        throw IllegalStateException("White king not found on the board")
    }

    fun getAllPieces(pieceColor: PieceColor): List<PieceType>{
        val resultPieces = mutableListOf<PieceType>()
        for (row in 0 until 8){
            for (col in 0 until 8){
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceColor == pieceColor){
                    resultPieces.add(square.pieceType!!)
                }
            }
        }
        return resultPieces
    }

    private fun regularMovePiece(sourcePosition: Position, destinationPosition: Position, isMoveReversed: Boolean = false){
        val sourceSquare = getSquare(sourcePosition.row, sourcePosition.col)
        val destinationSquare = getSquare(destinationPosition.row, destinationPosition.col)

        destinationSquare.isOccupied = true
        destinationSquare.pieceColor = sourceSquare.pieceColor
        destinationSquare.pieceType = sourceSquare.pieceType

        if (!isMoveReversed){
            sourceSquare.movePerformed()
            destinationSquare.movePerformed()
        } else {
            destinationSquare.moveReversed()
        }

        removePiece(sourcePosition)
    }

    private fun moveAndCapturePiece(sourcePosition: Position, destinationPosition: Position, capturedPieceColor: PieceColor, capturedPieceType: PieceType, isMoveReversed: Boolean = false){
        val sourceSquare = getSquare(sourcePosition.row, sourcePosition.col)
        val destinationSquare = getSquare(destinationPosition.row, destinationPosition.col)

        if (!isMoveReversed){
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = sourceSquare.pieceColor
            destinationSquare.pieceType = sourceSquare.pieceType

            sourceSquare.movePerformed()
            destinationSquare.movePerformed()
            removePiece(sourcePosition)
        } else {
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = sourceSquare.pieceColor
            destinationSquare.pieceType = sourceSquare.pieceType

            sourceSquare.isOccupied = true
            sourceSquare.pieceColor = capturedPieceColor
            sourceSquare.pieceType = capturedPieceType

            destinationSquare.moveReversed()
        }
    }

    private fun pawnPromotionMovePiece(sourcePosition: Position, destinationPosition: Position, promotedPieceColor: PieceColor, promotedPieceType: PieceType, isMoveReversed: Boolean = false){
        val sourceSquare = getSquare(sourcePosition.row, sourcePosition.col)
        val destinationSquare = getSquare(destinationPosition.row, destinationPosition.col)

        if (!isMoveReversed){
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = promotedPieceColor
            destinationSquare.pieceType = promotedPieceType

            sourceSquare.movePerformed()
            destinationSquare.movePerformed()
            removePiece(sourcePosition)
        } else {
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = promotedPieceColor
            destinationSquare.pieceType = PieceType.PAWN

            destinationSquare.moveReversed()
            removePiece(sourcePosition)
        }
    }

    private fun pawnPromotionCaptureMovePiece(sourcePosition: Position, destinationPosition: Position, promotedPieceColor: PieceColor, promotedPieceType: PieceType, capturedPieceColor: PieceColor, capturedPieceType: PieceType, isMoveReversed: Boolean = false){
        val sourceSquare = getSquare(sourcePosition.row, sourcePosition.col)
        val destinationSquare = getSquare(destinationPosition.row, destinationPosition.col)

        if (!isMoveReversed){
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = promotedPieceColor
            destinationSquare.pieceType = promotedPieceType

            sourceSquare.movePerformed()
            destinationSquare.movePerformed()
            removePiece(sourcePosition)
        } else {
            destinationSquare.isOccupied = true
            destinationSquare.pieceColor = promotedPieceColor
            destinationSquare.pieceType = PieceType.PAWN

            sourceSquare.isOccupied = true
            sourceSquare.pieceColor = capturedPieceColor
            sourceSquare.pieceType = capturedPieceType

            destinationSquare.moveReversed()
        }
    }

    private fun enPassantMovePiece(sourcePosition: Position, destinationPosition: Position, opponentPawnPosition: Position, isMoveReversed: Boolean = false){
        TODO("Not yet implemented")
    }

    private fun castleMovePiece(sourcePosition: Position, destinationPosition: Position, rookSourcePosition: Position, rookDestinationPosition: Position, isMoveReversed: Boolean = false){
        val sourceSquare = getSquare(sourcePosition.row, sourcePosition.col)
        val destinationSquare = getSquare(destinationPosition.row, destinationPosition.col)
        val rookSourceSquare = getSquare(rookSourcePosition.row, rookSourcePosition.col)
        val rookDestinationSquare = getSquare(rookDestinationPosition.row, rookDestinationPosition.col)

        destinationSquare.isOccupied = true
        destinationSquare.pieceColor = sourceSquare.pieceColor
        destinationSquare.pieceType = sourceSquare.pieceType

        removePiece(sourcePosition)

        rookDestinationSquare.isOccupied = true
        rookDestinationSquare.pieceColor = rookSourceSquare.pieceColor
        rookDestinationSquare.pieceType = rookSourceSquare.pieceType

        if (!isMoveReversed){
            sourceSquare.movePerformed()
            destinationSquare.movePerformed()
            rookSourceSquare.movePerformed()
            rookDestinationSquare.movePerformed()
        } else {
            destinationSquare.moveReversed()
            rookDestinationSquare.moveReversed()
        }

        removePiece(rookSourcePosition)
    }

    private fun removePiece(position: Position) {
        getSquare(position.row, position.col).clearSquare()
    }

    fun performMove(move: Move){
        when (move){
            is RegularMove -> {
                regularMovePiece(move.sourcePosition, move.destinationPosition)
            }
            is MoveAndCapture -> {
                moveAndCapturePiece(move.sourcePosition, move.destinationPosition,
                    move.capturedPieceColor, move.capturedPieceType)
            }
            is PawnPromotionMove -> {
                pawnPromotionMovePiece(move.sourcePosition, move.destinationPosition,
                    move.promotedPieceColor, move.promotedPieceType)
            }
            is PawnPromotionCaptureMove -> {
                pawnPromotionCaptureMovePiece(move.sourcePosition, move.destinationPosition,
                    move.promotedPieceColor, move.promotedPieceType,
                    move.capturedPieceColor, move.capturedPieceType)
            }
            is EnPassantMove -> {
                enPassantMovePiece(move.sourcePosition, move.destinationPosition,
                    move.opponentPawnPosition)
            }
            is CastleMove -> {
                castleMovePiece(move.sourcePosition, move.destinationPosition,
                    move.rookSourcePosition, move.rookDestinationPosition)
            }
        }
    }

    fun reverseMove(move: Move){
        when (move){
            is RegularMove -> {
                regularMovePiece(move.destinationPosition, move.sourcePosition,true)
            }
            is MoveAndCapture -> {
                moveAndCapturePiece(move.destinationPosition, move.sourcePosition,
                    move.capturedPieceColor, move.capturedPieceType, true)
            }
            is PawnPromotionMove -> {
                pawnPromotionMovePiece(move.destinationPosition, move.sourcePosition,
                    move.promotedPieceColor, move.promotedPieceType, true)
            }
            is PawnPromotionCaptureMove -> {
                pawnPromotionCaptureMovePiece(move.destinationPosition, move.sourcePosition,
                    move.promotedPieceColor, move.promotedPieceType,
                    move.capturedPieceColor, move.capturedPieceType, true)
            }
            is EnPassantMove -> {
                enPassantMovePiece(move.destinationPosition, move.sourcePosition,
                    move.opponentPawnPosition, true)
            }
            is CastleMove -> {
                castleMovePiece(move.destinationPosition, move.sourcePosition,
                    move.rookDestinationPosition, move.rookSourcePosition, true)
            }
        }
    }

    fun getStartingKingSquare(isPlayerStarted: Boolean, isForBot: Boolean): Square{
        if (isPlayerStarted && !isForBot){
            return getSquare(7, 4)
        } else if (isPlayerStarted) {
            return getSquare(0, 4)
        } else if (!isForBot){
            return getSquare(7, 3)
        } else {
            return getSquare(0, 3)
        }
    }

    fun getStartingKingSideRookSquare(isPlayerStarted: Boolean, isForBot: Boolean): Square{
        if (isPlayerStarted && !isForBot){
            return getSquare(7, 7)
        } else if (isPlayerStarted){
            return getSquare(0, 7)
        } else if (!isForBot){
            return getSquare(7, 0)
        } else {
            return getSquare(0, 0)
        }
    }

    fun getStartingQueenSideRookSquare(isPlayerStarted: Boolean, isForBot: Boolean): Square{
        if (isPlayerStarted && !isForBot){
            return getSquare(7, 0)
        } else if (isPlayerStarted){
            return getSquare(0, 0)
        } else if (!isForBot){
            return getSquare(7, 7)
        } else {
            return getSquare(0, 7)
        }
    }

    fun isKingInCheck(kingPosition: Square, kingColor: PieceColor): Boolean {
        val opponentColor = kingColor.opposite()

        if (isPawnThreat(kingPosition, opponentColor)) return true

        if (isKnightThreat(kingPosition, opponentColor)) return true

        if (isBishopThreat(kingPosition, opponentColor)) return true

        if (isRookThreat(kingPosition, opponentColor)) return true

        if (isQueenThreat(kingPosition, opponentColor)) return true

        if (isKingThreat(kingPosition, opponentColor)) return true

        return false
    }

    private fun isPawnThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        if (isValidSquare(kingPosition.row - 1, kingPosition.col - 1) &&
            getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceColor == opponentColor &&
            getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceType == PieceType.PAWN) {
            return true
        }
        if (isValidSquare(kingPosition.row - 1, kingPosition.col + 1) &&
            getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceColor == opponentColor &&
            getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceType == PieceType.PAWN) {
            return true
        }

        return false
    }

    private fun isKnightThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        val knightMoves = listOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )
        for ((rowOffset, colOffset) in knightMoves) {
            if (isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KNIGHT) {
                return true
            }
        }

        return false
    }

    private fun isBishopThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        val bishopMoves = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((rowOffset, colOffset) in bishopMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (isValidSquare(row, col)) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.BISHOP) {
                        return true
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        return false
    }

    private fun isRookThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        val rookMoves = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in rookMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (isValidSquare(row, col)) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.ROOK) {
                        return true
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        return false
    }

    private fun isQueenThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        val horizontalOffsets = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in horizontalOffsets) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (isValidSquare(row, col)) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.QUEEN) {
                        return true
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        val diagonalOffsets = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((rowOffset, colOffset) in diagonalOffsets) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (isValidSquare(row, col)) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.QUEEN) {
                        return true
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        return false
    }

    private fun isKingThreat(kingPosition: Square, opponentColor: PieceColor): Boolean{
        val kingMoves = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )
        for ((rowOffset, colOffset) in kingMoves) {
            if (isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KING) {
                return true
            }
        }

        return false
    }
}
