package com.example.chessmate.util.chess

class Chessboard {
    private var moveGenerator = LegalMoveGenerator()
    private val board: Array<Array<Square>> = Array(8) { row ->
        Array(8) { col ->
            Square(row, col)
        }
    }

    fun setupInitialBoard(isPlayerStarted: Boolean) {
        val backRow = arrayOf(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
            PieceType.QUEEN, PieceType.KING, PieceType.BISHOP,
            PieceType.KNIGHT, PieceType.ROOK
        )

        if (isPlayerStarted) {
            for (col in 0..7) {
                board[1][col].piece = Piece(PieceColor.BLACK, PieceType.PAWN)
                board[6][col].piece = Piece(PieceColor.WHITE, PieceType.PAWN)

                board[0][col].piece = Piece(PieceColor.BLACK, backRow[col])
                board[7][col].piece = Piece(PieceColor.WHITE, backRow[col])
            }
        } else {
            backRow.reverse()
            for (col in 0..7) {
                board[1][col].piece = Piece(PieceColor.WHITE, PieceType.PAWN)
                board[6][col].piece = Piece(PieceColor.BLACK, PieceType.PAWN)

                board[0][col].piece = Piece(PieceColor.WHITE, backRow[col])
                board[7][col].piece = Piece(PieceColor.BLACK, backRow[col])
            }
        }
    }

    fun copy(): Chessboard {
        val newBoard = Chessboard()
        for (row in 0..7) {
            for (col in 0..7) {
                newBoard.setSquare(row, col, board[row][col].copy())
            }
        }
        return newBoard
    }

    fun simulateMove(move: Move): Chessboard {
        val newBoard = this.copy()
        val fromSquare = newBoard.getSquare(move.from)
        val toSquare = newBoard.getSquare(move.to)

        toSquare.piece = fromSquare.piece.copy()
        fromSquare.clear()
        toSquare.piece.moved()

        return newBoard
    }

    fun isKingInCheck(color: PieceColor, isForBot: Boolean): Boolean {
        val kingPosition: Position = getKingPosition(color) ?: return false
        val opponentColor = color.opposite()
        return isPositionUnderAttack(kingPosition, opponentColor, !isForBot)
    }

    fun getKingPosition(color: PieceColor): Position? {
        var kingPosition: Position? = null
        forEachSquare { square, row, col ->
            if (square.piece.type == PieceType.KING && square.piece.color == color) {
                kingPosition = Position(row, col)
            }
        }

        return kingPosition
    }

    fun isPositionUnderAttack(position: Position?, attackerColor: PieceColor, isForBot: Boolean): Boolean {
        val opponentMoves = moveGenerator.generatePseudoLegalMovesForColor(this, attackerColor, isForBot)
        return opponentMoves.any { it.to == position }
    }

    private fun setSquare(row: Int, col: Int, square: Square) {
        board[row][col] = square
    }

    fun getSquare(row: Int, col: Int): Square = board[row][col]

    fun getSquare(position: Position): Square = board[position.row][position.col]

    fun isPositionValidAndEmpty(position: Position): Boolean {
        return isValid(position) && isEmpty(position)
    }

    fun isPositionValidAndEnemy(position: Position, color: PieceColor): Boolean {
        return isValid(position) && isEnemy(position, color)
    }

    fun isValid(position: Position): Boolean = position.row in 0..7 && position.col in 0..7

    fun isEmpty(position: Position): Boolean = getSquare(position).piece.type == PieceType.NONE

    fun isEnemy(position: Position, color: PieceColor): Boolean {
        val piece = getSquare(position).piece
        return piece.color == color.opposite()
    }

    fun isAlly(position: Position, color: PieceColor): Boolean {
        val piece = getSquare(position).piece
        return piece.color == color
    }

    fun forEachSquare(action: (Square, Int, Int) -> Unit) {
        for (row in 0..7) {
            for (col in 0..7) {
                action(board[row][col], row, col)
            }
        }
    }
}
