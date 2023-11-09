package com.example.chessmate.util.chess

class Chessboard {
    private val board: Array<Array<Square>> = Array(8) { Array(8) { Square(0, 0, false, null, null) } }

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

    fun getCheckingPieceSquare(chessboard: Chessboard, kingPosition: Square, kingColor: PieceColor): List<Square> {
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
