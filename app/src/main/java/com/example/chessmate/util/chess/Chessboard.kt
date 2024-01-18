package com.example.chessmate.util.chess

class Chessboard {
    private val board: Array<Array<Square>> = Array(8) { Array(8) { Square(0, 0, false, null, null) } }
    var moveTracker: MutableList<MoveTracker> = mutableListOf()

    init {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                board[row][col] = Square(row, col, false, null, null)
            }
        }
    }

    fun placePiece(row: Int, col: Int, pieceColor: PieceColor, pieceType: PieceType) {
        board[row][col] = Square(row, col, true, pieceColor, pieceType)
    }

    fun getSquare(row: Int, col: Int): Square {
        return board[row][col]
    }

    fun isValidSquare(row: Int, col: Int): Boolean{
        return row in 0 until 8 && col in 0 until 8
    }

    fun isEmptySquare(row: Int, col: Int): Boolean{
        return getSquare(row, col).pieceType == null
    }

    fun isOpponentPiece(row: Int, col: Int, botSquare: Square): Boolean{
        if (isEmptySquare(row, col)){
            return false
        }
        return getSquare(row, col).pieceColor != botSquare.pieceColor
    }

    fun evaluatePosition(): Int {
        var score = 0;

        score += evaluateMaterialBalance()

        score += evaluatePieceActivity()

        score += evaluatePawnStructure()

        score += evaluateKingSafety()

        return score
    }

    //evaluates king safety by checking how exposed the king is
    private fun evaluateKingSafety(): Int {
        var kingSafetyScore = 0

        val whiteKingSquare = getWhiteKingSquare()
        val blackKingSquare = getBlackKingSquare()

        if (whiteKingSquare != null && blackKingSquare != null) {
            kingSafetyScore += exposedKingPenalty(whiteKingSquare)
            kingSafetyScore -= exposedKingPenalty(blackKingSquare)
        }

        return kingSafetyScore
    }


    //penalizes the king based on number of attacking pieces
    private fun exposedKingPenalty(kingSquare: Square): Int {
        var attackingScore = 0

        val attackingPieces = getCheckingPieceSquare(this, kingSquare, kingSquare.pieceColor!!.opposite())
        for (attackingPiece in attackingPieces) {
            attackingScore += getPieceValue(attackingPiece.pieceType!!)
        }
        return -(attackingPieces.size + attackingScore)
    }

    //evaluates the pawn structure by checking if a pawn is isolated
    private fun evaluatePawnStructure(): Int {
        var pawnStructureScore = 0

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceType == PieceType.PAWN) {
                    if (square.pieceColor == PieceColor.WHITE) {
                        pawnStructureScore -= isolatedPawnPenalty(row, col)
                    } else {
                        pawnStructureScore += isolatedPawnPenalty(row, col)
                    }
                }
            }
        }

        return pawnStructureScore
    }

    //penalize isolated pawn by checking neighboring squares
    private fun isolatedPawnPenalty(row: Int, col: Int): Int {
        val neighbors = listOf(-1, 0, 1)
        for (rowOffset in neighbors) {
            for (colOffset in neighbors) {
                if (rowOffset != 0 || colOffset != 0) {
                    val neighborRow = row + rowOffset
                    val neighborCol = col + colOffset
                    if (isValidSquare(neighborRow, neighborCol) && getSquare(neighborRow, neighborCol).pieceType == PieceType.PAWN) {
                        return 0
                    }
                }
            }
        }
        return 1
    }

    //reward pieces that are in the center of the board
    //controlling the center
    private fun evaluatePieceActivity(): Int {
        var pieceActivityScore = 0

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    val pieceValue = getPieceValue(square.pieceType!!)
                    if (square.pieceColor == PieceColor.WHITE) {
                        pieceActivityScore += pieceValue * centerControlFactor(row, col)
                    } else {
                        pieceActivityScore -= pieceValue * centerControlFactor(row, col)
                    }
                }
            }
        }

        return pieceActivityScore
    }

    //give a higher score to squares in the center of the board
    private fun centerControlFactor(row: Int, col: Int): Int {
        val centerRows = arrayOf(3, 4)
        val centerCols = arrayOf(3, 4)
        return if (row in centerRows && col in centerCols) 1 else 0
    }

    //if the returned int is 0 it means that the game is equal material wise
    //if the returned int is less than 0 it means that black has material advantage
    //if the returned int is greater than 0 it means that white has material advantage
    private fun evaluateMaterialBalance(): Int {
        var materialScore = 0

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied) {
                    val pieceValue = getPieceValue(square.pieceType!!)
                    if (square.pieceColor == PieceColor.WHITE) {
                        materialScore += pieceValue
                    } else {
                        materialScore -= pieceValue
                    }
                }
            }
        }

        return materialScore
    }

    private fun getPieceValue(pieceType: PieceType): Int {
        return when (pieceType) {
            PieceType.PAWN -> 1
            PieceType.KNIGHT -> 3
            PieceType.BISHOP -> 3
            PieceType.ROOK -> 5
            PieceType.QUEEN -> 9
            PieceType.KING -> 0
        }
    }

    fun getWhiteKingSquare(): Square?{
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceColor == PieceColor.WHITE && square.pieceType == PieceType.KING) {
                    return square
                }
            }
        }
        return null
    }

    fun getBlackKingSquare(): Square?{
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = getSquare(row, col)
                if (square.isOccupied && square.pieceColor == PieceColor.BLACK && square.pieceType == PieceType.KING) {
                    return square
                }
            }
        }
        return null
    }

    fun getWhiteKingSideRook(): Square {
        return getSquare(7, 7)
    }
    fun getWhiteQueenSideRook(): Square{
        return getSquare(7,0)
    }
    fun getBlackQueenSideRook(): Square{
        return getSquare(7,7)
    }
    fun getBlackKingSideRook(): Square{
        return getSquare(7,0)
    }

    fun isKingInCheck(chessboard: Chessboard, kingPosition: Square, kingColor: PieceColor): Boolean {
        val opponentColor = if (kingColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        if (chessboard.isValidSquare(kingPosition.row - 1, kingPosition.col - 1) &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceColor == opponentColor &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceType == PieceType.PAWN) {
            return true
        }
        if (chessboard.isValidSquare(kingPosition.row - 1, kingPosition.col + 1) &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceColor == opponentColor &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceType == PieceType.PAWN) {
            return true
        }

        val knightMoves = listOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )
        for ((rowOffset, colOffset) in knightMoves) {
            if (chessboard.isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KNIGHT) {
                return true
            }
        }

        val queenMoves = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1), Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in queenMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
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

        val rookMoves = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in rookMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
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

        val bishopMoves = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((rowOffset, colOffset) in bishopMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
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

        val kingMoves = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )
        for ((rowOffset, colOffset) in kingMoves) {
            if (chessboard.isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KING) {
                return true
            }
        }

        return false
    }

    private fun getCheckingPieceSquare(chessboard: Chessboard, kingPosition: Square, kingColor: PieceColor): List<Square> {
        val opponentColor = if (kingColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        val checkingPieces = mutableListOf<Square>()

        if (chessboard.isValidSquare(kingPosition.row - 1, kingPosition.col - 1) &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceColor == opponentColor &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col - 1).pieceType == PieceType.PAWN) {
            checkingPieces.add(chessboard.getSquare(kingPosition.row - 1, kingPosition.col - 1))
        }
        if (chessboard.isValidSquare(kingPosition.row - 1, kingPosition.col + 1) &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceColor == opponentColor &&
            chessboard.getSquare(kingPosition.row - 1, kingPosition.col + 1).pieceType == PieceType.PAWN) {
            checkingPieces.add(chessboard.getSquare(kingPosition.row - 1, kingPosition.col + 1))
        }

        val knightMoves = listOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )
        for ((rowOffset, colOffset) in knightMoves) {
            if (chessboard.isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KNIGHT) {
                checkingPieces.add(chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset))
            }
        }

        val queenMoves = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1), Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in queenMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.QUEEN) {
                        checkingPieces.add(chessboard.getSquare(row, col))
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        val rookMoves = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in rookMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.ROOK) {
                        checkingPieces.add(chessboard.getSquare(row, col))
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        val bishopMoves = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((rowOffset, colOffset) in bishopMoves) {
            var row = kingPosition.row + rowOffset
            var col = kingPosition.col + colOffset
            while (chessboard.isValidSquare(row, col)) {
                val square = chessboard.getSquare(row, col)
                if (square.isOccupied) {
                    if (square.pieceColor == opponentColor && square.pieceType == PieceType.BISHOP) {
                        checkingPieces.add(chessboard.getSquare(row, col))
                    }
                    break
                }
                row += rowOffset
                col += colOffset
            }
        }

        val kingMoves = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )
        for ((rowOffset, colOffset) in kingMoves) {
            if (chessboard.isValidSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset) &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceColor == opponentColor &&
                chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset).pieceType == PieceType.KING) {
                checkingPieces.add(chessboard.getSquare(kingPosition.row + rowOffset, kingPosition.col + colOffset))
            }
        }

        return checkingPieces
    }
}
