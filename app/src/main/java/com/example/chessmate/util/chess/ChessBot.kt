package com.example.chessmate.util.chess

class ChessBot(private val botColor: PieceColor, private val depth: Int, private var chessboard: Chessboard){
    private lateinit var clonedBoard: Chessboard

    init {
        clonedBoard = chessboard.cloneBoardWithoutUI()
    }

    fun getBestMove(): Move?{
        //before generating moves you need to check if the chessbot king is in check
        clonedBoard = chessboard.cloneBoardWithoutUI()
        return findBestMoves()
    }

    private fun findBestMoves(): Move?{
        val legalMoves = generateLegalMoves()
        var tmpScore: Int = Int.MAX_VALUE
        var bestMove: Move? = null
        for (l in legalMoves){
            if (l.score < tmpScore){
                tmpScore = l.score
            }
        }
        for (l in legalMoves){
            if (tmpScore == l.score){
                bestMove = l
            }
        }

        return bestMove
    }

    private fun generateLegalMoves(): List<Move>{
        val legalMoves = mutableListOf<Move>()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = clonedBoard.getSquare(row, col)
                if (piece.pieceColor == botColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(row, col))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(row, col))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(row, col))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(row, col))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(row, col))
                        PieceType.KING -> legalMoves.addAll(generateKingMoves(row, col))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    private fun generatePawnMoves(row: Int, col: Int): List<Move>{
        clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        val startSquare: Square = clonedBoard.getSquare(row, col)
        var destSquare: Square
        val direction = 1

        if (clonedBoard.isEmptySquare(row + direction, col)){
            destSquare = clonedBoard.getSquare(row + direction, col)
            startSquare.move(destSquare)
            legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }

        if (row == 1){
            if (clonedBoard.isEmptySquare(row + direction, col) &&
                clonedBoard.isEmptySquare(row + 2 * direction, col)){
                destSquare = clonedBoard.getSquare(row + 2 * direction, col)
                startSquare.move(destSquare)
                legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        if (clonedBoard.isValidSquare(row + direction, col - direction) &&
            clonedBoard.isOpponentPiece(row + direction, col - direction, botColor)){
            destSquare = clonedBoard.getSquare(row + direction, col - direction)
            startSquare.move(destSquare)
            legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }
        if (clonedBoard.isValidSquare(row + direction, col + direction) &&
            clonedBoard.isOpponentPiece(row + direction, col + direction, botColor)){
            destSquare = clonedBoard.getSquare(row + direction, col + direction)
            startSquare.move(destSquare)
            legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }

        return legalMoves
    }

    private fun generateKnightMoves(row: Int, col: Int): List<Move>{
        clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        val startSquare = clonedBoard.getSquare(row, col)
        var destSquare: Square
        val moves = arrayOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )

        for ((rowOffset, colOffset) in moves){
            val newRow = row + rowOffset
            val newCol = col + colOffset

            if (clonedBoard.isValidSquare(newRow, newCol) &&
                (clonedBoard.isEmptySquare(newRow,newCol) || clonedBoard.isOpponentPiece(newRow, newCol, botColor))){
                destSquare = clonedBoard.getSquare(newRow, newCol)
                startSquare.move(destSquare)
                legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        return legalMoves
    }

    private fun generateBishopMoves(row: Int, col: Int): List<Move>{
        clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        val startSquare = clonedBoard.getSquare(row, col)
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (clonedBoard.isValidSquare(newRow, newCol)){
                destSquare = clonedBoard.getSquare(newRow, newCol)
                if (clonedBoard.isEmptySquare(newRow, newCol)){
                    startSquare.move(destSquare)
                    legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                    clonedBoard = chessboard.cloneBoardWithoutUI()
                } else if (clonedBoard.isOpponentPiece(newRow, newCol, botColor)){
                    startSquare.move(destSquare)
                    legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                    clonedBoard = chessboard.cloneBoardWithoutUI()
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

    private fun generateRookMoves(row: Int, col: Int): List<Move>{
        clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        val startSquare = clonedBoard.getSquare(row, col)
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, 0), Pair(1, 0),
            Pair(0, -1), Pair(0, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (clonedBoard.isValidSquare(newRow, newCol)){
                destSquare = clonedBoard.getSquare(newRow, newCol)
                if (clonedBoard.isEmptySquare(newRow, newCol)){
                    startSquare.move(destSquare)
                    legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                    clonedBoard = chessboard.cloneBoardWithoutUI()
                } else if (clonedBoard.isOpponentPiece(newRow, newCol, botColor)){
                    startSquare.move(destSquare)
                    legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                    clonedBoard = chessboard.cloneBoardWithoutUI()
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

    private fun generateQueenMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()

        legalMoves.addAll(generateBishopMoves(row, col))
        legalMoves.addAll(generateRookMoves(row, col))

        return legalMoves
    }

    private fun generateKingMoves(row: Int, col: Int): List<Move>{
        clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        val startSquare = clonedBoard.getSquare(row, col)
        var destSquare: Square
        val moves = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in moves){
            val newRow = row + rowOffset
            val newCol = col + colOffset

            if (clonedBoard.isValidSquare(newRow, newCol) &&
                (clonedBoard.isEmptySquare(newRow, newCol) || clonedBoard.isOpponentPiece(newRow, newCol, botColor))){
                destSquare = clonedBoard.getSquare(newRow, newCol)
                startSquare.move(destSquare)
                legalMoves.add(Move(startSquare, destSquare, clonedBoard.evaluatePosition()))
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        return legalMoves
    }
}