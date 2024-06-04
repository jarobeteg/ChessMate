package com.example.chessmate.util.chess

class PieceSquareTables {

    val whitePawnsForPlayer = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F),
        floatArrayOf(1.0F, 1.0F, 2.0F, 3.0F, 3.0F, 2.0F, 1.0F, 1.0F),
        floatArrayOf(0.5F, 0.5F, 1.0F, 2.5F, 2.5F, 1.0F, 0.5F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, -0.5F, -1.0F, 0.0F, 0.0F, -1.0F, -0.5F, 0.5F),
        floatArrayOf(0.5F, 1.0F, 1.0F, -2.0F, -2.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
    )

    val whiteKnightsForPlayer = arrayOf(
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-3.0F, 0.0F, 1.0F, 1.5F, 1.5F, 1.0F, 0.0F, -3.0F),
        floatArrayOf(-3.0F, 0.5F, 1.5F, 2.0F, 2.0F, 1.5F, 0.5F, -3.0F),
        floatArrayOf(-3.0F, 0.0F, 1.5F, 2.0F, 2.0F, 1.5F, 0.0F, -3.0F),
        floatArrayOf(-3.0F, 0.5F, 1.0F, 1.5F, 1.5F, 1.0F, 0.5F, -3.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.5F, 0.5F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F)
    )

    val whiteBishopsForPlayer = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F),
        floatArrayOf(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F)
    )

    val whiteRooksForPlayer = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F)
    )

    val whiteQueenForPlayer = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F),
        floatArrayOf(0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F),
        floatArrayOf(-1.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F)
    )

    val whiteKingMidGameForPlayer = arrayOf(
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-2.0F, -3.0F, -3.0F, -4.0F, -4.0F, -3.0F, -3.0F, -2.0F),
        floatArrayOf(-1.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -1.0F),
        floatArrayOf(2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F),
        floatArrayOf(2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 2.0F)
    )

    val whiteKingEndGameForPlayer = arrayOf(
        floatArrayOf(-5.0F, -4.0F, -3.0F, -2.0F, -2.0F, -3.0F, -4.0F, -5.0F),
        floatArrayOf(-3.0F, -2.0F, -1.0F, 0.0F, 0.0F, -1.0F, -2.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.0F, -3.0F),
        floatArrayOf(-5.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -5.0F)
    )
}