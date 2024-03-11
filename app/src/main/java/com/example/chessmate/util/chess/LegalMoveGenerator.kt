package com.example.chessmate.util.chess

class LegalMoveGenerator{
    private var gameManager = GameManager()

    fun generateLegalMovesForBot(chessboard: Chessboard, pieceColor: PieceColor): List<Move>{
        val legalMoves = mutableListOf<Move>()
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = clonedBoard.getSquare(row, col)
                if (piece.pieceColor == pieceColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(row, col, chessboard, pieceColor, true))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(row, col, chessboard, pieceColor, true))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(row, col, chessboard, pieceColor, true))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(row, col, chessboard, pieceColor, true))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(row, col, chessboard, pieceColor, true))
                        PieceType.KING -> legalMoves.addAll(generateKingMoves(row, col, chessboard, pieceColor, true))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    fun generateLegalMovesForPlayer(chessboard: Chessboard, pieceColor: PieceColor): List<Move>{
        val legalMoves = mutableListOf<Move>()
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = clonedBoard.getSquare(row, col)
                if (piece.pieceColor == pieceColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(row, col, chessboard, pieceColor))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(row, col, chessboard, pieceColor))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(row, col, chessboard, pieceColor))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(row, col, chessboard, pieceColor))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(row, col, chessboard, pieceColor))
                        PieceType.KING -> legalMoves.addAll(generateKingMoves(row, col, chessboard, pieceColor))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    fun generatePawnMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        var startSquare: Square
        var destSquare: Square
        val direction = if (isForBot) 1 else -1

        if (clonedBoard.isEmptySquare(row + direction, col)){
            startSquare = clonedBoard.getSquare(row, col)
            destSquare = clonedBoard.getSquare(row + direction, col)
            startSquare.move(destSquare)
            if (!gameManager.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
            }
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }

        if (row == 1 && isForBot){
            if (clonedBoard.isEmptySquare(row + direction, col) &&
                clonedBoard.isEmptySquare(row + 2 * direction, col)){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(row + 2 * direction, col)
                startSquare.move(destSquare)
                if (!gameManager.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                    legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                }
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        if (row == 6 && !isForBot){
            if (clonedBoard.isEmptySquare(row + direction, col) &&
                clonedBoard.isEmptySquare(row + 2 * direction, col)){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(row + 2 * direction, col)
                startSquare.move(destSquare)
                if (!gameManager.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                    legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                }
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        if (clonedBoard.isValidSquare(row + direction, col - direction) &&
            clonedBoard.isOpponentPiece(row + direction, col - direction, pieceColor)){
            startSquare = clonedBoard.getSquare(row, col)
            destSquare = clonedBoard.getSquare(row + direction, col - direction)
            startSquare.move(destSquare)
            if (!gameManager.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
            }
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }

        if (clonedBoard.isValidSquare(row + direction, col + direction) &&
            clonedBoard.isOpponentPiece(row + direction, col + direction, pieceColor)){
            startSquare = clonedBoard.getSquare(row, col)
            destSquare = clonedBoard.getSquare(row + direction, col + direction)
            startSquare.move(destSquare)
            if (!gameManager.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
            }
            clonedBoard = chessboard.cloneBoardWithoutUI()
        }

        return legalMoves
    }

    fun generateKnightMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        var startSquare: Square
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
                (clonedBoard.isEmptySquare(newRow,newCol) || clonedBoard.isOpponentPiece(newRow, newCol, pieceColor))){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(newRow, newCol)
                startSquare.move(destSquare)
                if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                    legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                }
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        return legalMoves
    }

    fun generateBishopMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        var startSquare: Square
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (clonedBoard.isValidSquare(newRow, newCol)){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(newRow, newCol)
                if (clonedBoard.isEmptySquare(newRow, newCol)){
                    startSquare.move(destSquare)
                    if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                        legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                    }
                    clonedBoard = chessboard.cloneBoardWithoutUI()
                } else if (clonedBoard.isOpponentPiece(newRow, newCol, pieceColor)){
                    startSquare.move(destSquare)
                    if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                        legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                    }
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

    fun generateRookMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        var startSquare: Square
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, 0), Pair(1, 0),
            Pair(0, -1), Pair(0, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (clonedBoard.isValidSquare(newRow, newCol)){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(newRow, newCol)
                if (clonedBoard.isEmptySquare(newRow, newCol)){
                    startSquare.move(destSquare)
                    if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                        legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                    }
                    clonedBoard = chessboard.cloneBoardWithoutUI()
                } else if (clonedBoard.isOpponentPiece(newRow, newCol, pieceColor)){
                    startSquare.move(destSquare)
                    if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                        legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                    }
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

    fun generateQueenMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        val legalMoves = mutableListOf<Move>()

        legalMoves.addAll(generateBishopMoves(row, col, chessboard, pieceColor, isForBot))
        legalMoves.addAll(generateRookMoves(row, col, chessboard, pieceColor, isForBot))

        return legalMoves
    }

    fun generateKingMoves(row: Int, col: Int, chessboard: Chessboard, pieceColor: PieceColor, isForBot: Boolean = false): List<Move>{
        var clonedBoard = chessboard.cloneBoardWithoutUI()

        val legalMoves = mutableListOf<Move>()
        var startSquare: Square
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
                (clonedBoard.isEmptySquare(newRow, newCol) || clonedBoard.isOpponentPiece(newRow, newCol, pieceColor))){
                startSquare = clonedBoard.getSquare(row, col)
                destSquare = clonedBoard.getSquare(newRow, newCol)
                startSquare.move(destSquare)
                if (!clonedBoard.isKingInCheck(clonedBoard, clonedBoard.getKingSquare(pieceColor)!!, pieceColor)){
                    legalMoves.add(Move(startSquare.row, startSquare.col, destSquare.row, destSquare.col, clonedBoard.evaluatePosition()))
                }
                clonedBoard = chessboard.cloneBoardWithoutUI()
            }
        }

        return legalMoves
    }
}