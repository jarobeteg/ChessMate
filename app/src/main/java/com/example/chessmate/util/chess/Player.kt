package com.example.chessmate.util.chess

class Player(private val chessboard: Chessboard, private val chessboardEvaluator: ChessboardEvaluator,
             private val legalMoveGenerator: LegalMoveGenerator, val playerColor: PieceColor) {
    //need to evaluate the move for the player and check if the player had a better move
    var isPlayerTurn: Boolean = false
    var legalPlayerMoves: MutableList<Move> = mutableListOf()

    fun calculateLegalMoves(square: Square): MutableList<Move>{
        var copiedChessboard = chessboard.cloneBoardWithoutUI()
        val legalMoves = when(square.pieceType){
            PieceType.PAWN -> legalMoveGenerator.generatePawnMoves(copiedChessboard, square.row, square.col, playerColor)
            PieceType.ROOK -> legalMoveGenerator.generateRookMoves(copiedChessboard, square.row, square.col, playerColor)
            PieceType.KNIGHT -> legalMoveGenerator.generateKnightMoves(copiedChessboard, square.row, square.col, playerColor)
            PieceType.BISHOP -> legalMoveGenerator.generateBishopMoves(copiedChessboard, square.row, square.col, playerColor)
            PieceType.QUEEN -> legalMoveGenerator.generateQueenMoves(copiedChessboard, square.row, square.col, playerColor)
            PieceType.KING -> legalMoveGenerator.generateKingMoves(copiedChessboard, square.row, square.col, playerColor)
            else -> mutableListOf()
        }

        legalPlayerMoves = legalMoves
        return legalMoves
    }
}