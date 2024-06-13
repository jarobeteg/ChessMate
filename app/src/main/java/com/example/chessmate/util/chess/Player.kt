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

//    private fun convertMoves(legalMoves: MutableList<Move>): MutableList<Move> {
//        for (move in legalMoves) {
//            when (move) {
//                is RegularMove -> convertRegularMove(move)
//                is CastleMove -> convertCastleMove(move)
//                is EnPassantMove -> convertEnPassantMove(move)
//                is MoveAndCapture -> convertMoveAndCapture(move)
//                is PawnPromotionMove -> convertPawnPromotionMove(move)
//                is PawnPromotionCaptureMove -> convertPawnPromotionCaptureMove(move)
//            }
//        }
//        return legalPlayerMoves
//    }
//
//    private fun convertRegularMove(move: RegularMove){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val sourcePieceColor = sourceSquare.pieceColor
//        val sourcePieceType = sourceSquare.pieceType
//        val score = move.score
//
//        val regularMove = RegularMove(sourceSquare, destinationSquare, sourcePieceColor, sourcePieceType, score)
//        legalPlayerMoves.add(regularMove)
//    }
//
//    private fun convertMoveAndCapture(move: MoveAndCapture){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val sourcePieceColor = sourceSquare.pieceColor
//        val sourcePieceType = sourceSquare.pieceType
//        val capturedPieceColor = move.capturedPieceColor
//        val capturedPieceType = move.capturedPieceType
//        val score = move.score
//
//        val moveAndCapture = MoveAndCapture(
//            sourceSquare,
//            destinationSquare,
//            sourcePieceColor,
//            sourcePieceType,
//            capturedPieceColor,
//            capturedPieceType,
//            score
//        )
//        legalPlayerMoves.add(moveAndCapture)
//    }
//
//    private fun convertPawnPromotionMove(move: PawnPromotionMove){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val promotedPieceColor = move.promotedPieceColor
//        val promotedPieceType = move.promotedPieceType
//        val score = move.score
//
//        val pawnPromotionMove =
//            PawnPromotionMove(sourceSquare, destinationSquare, promotedPieceColor, promotedPieceType, score)
//        legalPlayerMoves.add(pawnPromotionMove)
//    }
//
//    private fun convertPawnPromotionCaptureMove(move: PawnPromotionCaptureMove){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val promotedPieceColor = move.promotedPieceColor
//        val promotedPieceType = move.promotedPieceType
//        val capturedPieceColor = move.capturedPieceColor
//        val capturedPieceType = move.capturedPieceType
//        val score = move.score
//
//        val pawnPromotionCaptureMove = PawnPromotionCaptureMove(sourceSquare, destinationSquare, promotedPieceColor, promotedPieceType, capturedPieceColor, capturedPieceType, score)
//        legalPlayerMoves.add(pawnPromotionCaptureMove)
//    }
//
//    private fun convertCastleMove(move: CastleMove){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val sourcePieceColor = sourceSquare.pieceColor
//        val rookSourceSquare =
//            chessboard.getSquare(move.rookSourceSquare.row, move.rookSourceSquare.col)
//        val rookDestinationSquare = chessboard.getSquare(
//            move.rookDestinationSquare.row,
//            move.rookDestinationSquare.col
//        )
//        val isKingSideCastles = move.isKingSideCastles
//        val score = move.score
//
//        val castleMove = CastleMove(
//            sourceSquare,
//            destinationSquare,
//            sourcePieceColor,
//            rookSourceSquare,
//            rookDestinationSquare,
//            isKingSideCastles,
//            score
//        )
//        legalPlayerMoves.add(castleMove)
//    }
//
//    private fun convertEnPassantMove(move: EnPassantMove){
//        val sourceSquare =
//            chessboard.getSquare(move.sourceSquare.row, move.sourceSquare.col)
//        val destinationSquare =
//            chessboard.getSquare(move.destinationSquare.row, move.destinationSquare.col)
//        val sourcePieceColor = sourceSquare.pieceColor
//        val opponentPawnSquare = chessboard.getSquare(
//            move.opponentPawnSquare.row,
//            move.opponentPawnSquare.col
//        )
//        val score = move.score
//
//        val enPassantMove =
//            EnPassantMove(sourceSquare, destinationSquare, sourcePieceColor, opponentPawnSquare, score)
//        legalPlayerMoves.add(enPassantMove)
//    }
}