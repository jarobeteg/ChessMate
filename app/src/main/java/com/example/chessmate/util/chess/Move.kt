package com.example.chessmate.util.chess

sealed class Move {
    abstract val sourceSquare: Square
    abstract val destinationSquare: Square
    abstract var score: Float
}

data class RegularMove(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    override var score: Float = 0.00F
): Move()

data class MoveAndCapture(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    val capturedPieceColor: PieceColor,
    val capturedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class PawnPromotionMove(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    val promotedPieceColor: PieceColor,
    val promotedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class PawnPromotionCaptureMove(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    val promotedPieceColor: PieceColor,
    val promotedPieceType: PieceType,
    val capturedPieceColor: PieceColor,
    val capturedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class EnPassantMove(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    val opponentPawnSquare: Square,
    override var score: Float = 0.00F
): Move()

data class CastleMove(
    override val sourceSquare: Square,
    override val destinationSquare: Square,
    val rookSourceSquare: Square,
    val rookDestinationSquare: Square,
    override var score: Float = 0.00F
): Move()

