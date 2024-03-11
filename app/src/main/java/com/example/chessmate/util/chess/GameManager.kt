package com.example.chessmate.util.chess

class GameManager {

    fun isKingInCheck(chessboard: Chessboard, kingPosition: Square, kingColor: PieceColor): Boolean {
        val opponentColor = kingColor.opposite()

        if (isPawnThreat(chessboard, kingPosition, opponentColor)) return true

        if (isKnightThreat(chessboard, kingPosition, opponentColor)) return true

        if (isBishopThreat(chessboard, kingPosition, opponentColor)) return true

        if (isRookThreat(chessboard, kingPosition, opponentColor)) return true

        if (isQueenThreat(chessboard, kingPosition, opponentColor)) return true

        if (isKingThreat(chessboard, kingPosition, opponentColor)) return true

        return false
    }

    private fun isPawnThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
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

        return false
    }

    private fun isKnightThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
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

        return false
    }

    private fun isBishopThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
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

        return false
    }

    private fun isRookThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
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

        return false
    }

    private fun isQueenThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
        val horizontalOffsets = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for ((rowOffset, colOffset) in horizontalOffsets) {
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

        val diagonalOffsets = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        for ((rowOffset, colOffset) in diagonalOffsets) {
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

        return false
    }

    private fun isKingThreat(chessboard: Chessboard, kingPosition: Square, opponentColor: PieceColor): Boolean{
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
}