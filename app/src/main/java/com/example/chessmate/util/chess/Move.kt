package com.example.chessmate.util.chess

sealed class Move {
    abstract val sourcePosition: Position
    abstract val destinationPosition: Position
    abstract var score: Float
}

data class RegularMove(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val sourcePieceColor: PieceColor,
    val sourcePieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class MoveAndCapture(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val sourcePieceColor: PieceColor,
    val sourcePieceType: PieceType,
    val capturedPieceColor: PieceColor,
    val capturedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class PawnPromotionMove(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val promotedPieceColor: PieceColor,
    val promotedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class PawnPromotionCaptureMove(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val promotedPieceColor: PieceColor,
    val promotedPieceType: PieceType,
    val capturedPieceColor: PieceColor,
    val capturedPieceType: PieceType,
    override var score: Float = 0.00F
): Move()

data class EnPassantMove(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val sourcePieceColor: PieceColor,
    val opponentPawnPosition: Position,
    override var score: Float = 0.00F
): Move()

data class CastleMove(
    override val sourcePosition: Position,
    override val destinationPosition: Position,
    val sourcePieceColor: PieceColor,
    val rookSourcePosition: Position,
    val rookDestinationPosition: Position,
    val isKingSideCastles: Boolean,
    override var score: Float = 0.00F
): Move()

