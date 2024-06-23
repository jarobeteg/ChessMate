package com.example.chessmate.util.chess

class LegalMoveGenerator {
    private val knightMoves = arrayOf(
        Position(-2,-1), Position(-1,-2), Position(1,-2), Position(2,-1),
        Position(2,1), Position(1,2), Position(-1,2), Position(-2,1)
    )

    private val bishopMoves = arrayOf(
        Position(-1,-1), Position(-1,1),
        Position(1,-1), Position(1,1)
    )

    private val rookMoves = arrayOf(
        Position(-1,0), Position(1,0),
        Position(0,-1), Position(0,1)
    )

    private val queenMoves = arrayOf(
        Position(-1,0), Position(1,0),
        Position(0,-1), Position(0,1),
        Position(-1,-1), Position(-1,1),
        Position(1,-1), Position(1,1)
    )

    private val kingMoves = arrayOf(
        Position(-1,-1), Position(-1,0), Position(-1,1),
        Position(0,-1), Position(0,1),
        Position(1,-1), Position(1,0), Position(1,1)
    )

    fun generateAllMovesForBot(chessboard: Chessboard, color: PieceColor): List<Move> {
        val moves = mutableListOf<Move>()
        val direction = 1
        val startRow = 1
        val promotionRow = 7

        chessboard.forEachSquare { square, row, col ->
            val pieceType = square.piece.type
            val pieceColor = square.piece.color
            val position = Position(row, col)

            when (pieceColor to pieceType) {
                color to PieceType.PAWN -> generateMovesForPawn(chessboard, position, color, direction, startRow, promotionRow, moves)
                color to PieceType.KNIGHT -> generateMovesForKnight(chessboard, position, color, moves)
                color to PieceType.BISHOP -> generateMovesForBishop(chessboard, position, color, moves)
                color to PieceType.ROOK -> generateMovesForRook(chessboard, position, color, moves)
                color to PieceType.QUEEN -> generateMovesForQueen(chessboard, position, color, moves)
                color to PieceType.KING -> generateMovesForKing(chessboard, position, color, moves)
                else -> {}
            }
        }

        return moves.filter { move ->
            val newBoard = chessboard.simulateMove(move)
            !newBoard.isKingInCheck(color, true)
        }
    }

    fun generateAllMovesForPlayer(chessboard: Chessboard, color: PieceColor): List<Move> {
        val moves = mutableListOf<Move>()
        val direction = -1
        val startRow = 6
        val promotionRow = 0

        chessboard.forEachSquare { square, row, col ->
            val pieceType = square.piece.type
            val pieceColor = square.piece.color
            val position = Position(row, col)

            when (pieceColor to pieceType) {
                color to PieceType.PAWN -> generateMovesForPawn(chessboard, position, color, direction, startRow, promotionRow, moves)
                color to PieceType.KNIGHT -> generateMovesForKnight(chessboard, position, color, moves)
                color to PieceType.BISHOP -> generateMovesForBishop(chessboard, position, color, moves)
                color to PieceType.ROOK -> generateMovesForRook(chessboard, position, color, moves)
                color to PieceType.QUEEN -> generateMovesForQueen(chessboard, position, color, moves)
                color to PieceType.KING -> generateMovesForKing(chessboard, position, color, moves)
                else -> {}
            }
        }

        return moves.filter { move ->
            val newBoard = chessboard.simulateMove(move)
            !newBoard.isKingInCheck(color, false)
        }
    }

    fun generatePseudoLegalMovesForColor(chessboard: Chessboard, color: PieceColor, isForBot: Boolean): List<Move> {
        val moves = mutableListOf<Move>()
        val direction = if (isForBot) 1 else -1
        val startRow = if (isForBot) 1 else 6
        val promotionRow = if (isForBot) 7 else 0

        chessboard.forEachSquare { square, row, col ->
            val pieceType = square.piece.type
            val pieceColor = square.piece.color
            val position = Position(row, col)

            when (pieceColor to pieceType) {
                color to PieceType.PAWN -> generateMovesForPawn(chessboard, position, color, direction, startRow, promotionRow, moves)
                color to PieceType.KNIGHT -> generateMovesForKnight(chessboard, position, color, moves)
                color to PieceType.BISHOP -> generateMovesForBishop(chessboard, position, color, moves)
                color to PieceType.ROOK -> generateMovesForRook(chessboard, position, color, moves)
                color to PieceType.QUEEN -> generateMovesForQueen(chessboard, position, color, moves)
                color to PieceType.KING -> generateMovesForKing(chessboard, position, color, moves)
                else -> {}
            }
        }

        return moves
    }

    fun generateMovesForPawn(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        direction: Int,
        startRow: Int,
        promotionRow: Int,
        moves: MutableList<Move>
    ) {
        val row = position.row
        val col = position.col

        val oneStepForward = Position(row + direction, col)
        val piece = Piece(color, PieceType.PAWN)
        if (chessboard.isPositionValidAndEmpty(oneStepForward)) {
            if (oneStepForward.row == promotionRow) {
                moves.addAll(generatePromotionMoves(position, oneStepForward, color))
            } else {
                moves.add(Move(position, oneStepForward, piece))
            }

            if (row == startRow) {
                val twoStepForward = Position(row + direction * 2, col)
                if (chessboard.isPositionValidAndEmpty(twoStepForward)) {
                    moves.add(Move(position, twoStepForward, piece))
                }
            }
        }

        arrayOf(-1, 1).forEach { offset ->
            val capturePos = Position(row + direction, col + offset)
            if (chessboard.isPositionValidAndEnemy(capturePos, color)) {
                if (capturePos.row == promotionRow) {
                    moves.addAll(generatePromotionMoves(position, capturePos, color))
                } else {
                    val capturedPiece = chessboard.getSquare(capturePos).piece
                    moves.add(Move(position, capturePos, piece, capturedPiece))
                }
            }
        }
    }

    private fun generatePromotionMoves(from: Position, to: Position, color: PieceColor): List<Move> {
        val promotionTypes = listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)
        return promotionTypes.map { promotionType -> Move(from, to, Piece(color, PieceType.PAWN), promotion = promotionType) }
    }

    fun generateMovesForKnight(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        moves: MutableList<Move>
    ) {
        for (move in knightMoves) {
            val newRow = position.row + move.row
            val newCol = position.col + move.col

            val newPosition = Position(newRow, newCol)

            val piece = Piece(color, PieceType.KNIGHT)
            if (chessboard.isPositionValidAndEmpty(newPosition)) {
                moves.add(Move(position, newPosition, piece))
            } else if (chessboard.isPositionValidAndEnemy(newPosition, color)) {
                val capturedPiece = chessboard.getSquare(newPosition).piece
                moves.add(Move(position, newPosition, piece, capturedPiece))
            }
        }
    }

    fun generateMovesForBishop(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        moves: MutableList<Move>
    ) {
        for (move in bishopMoves) {
            var newRow = position.row
            var newCol = position.col

            while (true) {
                newRow += move.row
                newCol += move.col

                val newPosition = Position(newRow, newCol)

                if (!chessboard.isValid(newPosition)) break
                if (chessboard.isAlly(newPosition, color)) break

                val piece = Piece(color, PieceType.BISHOP)
                if (chessboard.isEmpty(newPosition)) {
                    moves.add(Move(position, newPosition, piece))
                } else if (chessboard.isEnemy(newPosition, color)) {
                    val capturedPiece = chessboard.getSquare(newPosition).piece
                    moves.add(Move(position, newPosition, piece, capturedPiece))
                    break
                }
            }
        }
    }

    fun generateMovesForRook(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        moves: MutableList<Move>
    ) {
        for (move in rookMoves) {
            var newRow = position.row
            var newCol = position.col

            while (true) {
                newRow += move.row
                newCol += move.col

                val newPosition = Position(newRow, newCol)

                if (!chessboard.isValid(newPosition)) break
                if (chessboard.isAlly(newPosition, color)) break

                val piece = Piece(color, PieceType.ROOK)
                if (chessboard.isEmpty(newPosition)) {
                    moves.add(Move(position, newPosition, piece))
                } else if (chessboard.isEnemy(newPosition, color)) {
                    val capturedPiece = chessboard.getSquare(newPosition).piece
                    moves.add(Move(position, newPosition, piece, capturedPiece))
                    break
                }
            }
        }
    }

    fun generateMovesForQueen(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        moves: MutableList<Move>
    ) {
        for (move in queenMoves) {
            var newRow = position.row
            var newCol = position.col

            while (true) {
                newRow += move.row
                newCol += move.col

                val newPosition = Position(newRow, newCol)

                if (!chessboard.isValid(newPosition)) break
                if (chessboard.isAlly(newPosition, color)) break

                val piece = Piece(color, PieceType.QUEEN)
                if (chessboard.isEmpty(newPosition)) {
                    moves.add(Move(position, newPosition, piece))
                } else if (chessboard.isEnemy(newPosition, color)) {
                    val capturedPiece = chessboard.getSquare(newPosition).piece
                    moves.add(Move(position, newPosition, piece, capturedPiece))
                    break
                }
            }
        }
    }

    fun generateMovesForKing(
        chessboard: Chessboard,
        position: Position,
        color: PieceColor,
        moves: MutableList<Move>
    ) {
        for (move in kingMoves) {
            val newRow = position.row + move.row
            val newCol = position.col + move.col

            val newPosition = Position(newRow, newCol)

            if (!chessboard.isValid(newPosition)) break

            val piece = Piece(color, PieceType.KING)
            if (chessboard.isEmpty(newPosition)) {
                moves.add(Move(position, newPosition, piece))
            } else if (chessboard.isEnemy(newPosition, color)) {
                val capturedPiece = chessboard.getSquare(newPosition).piece
                moves.add(Move(position, newPosition, piece, capturedPiece))
            }
        }
    }
}