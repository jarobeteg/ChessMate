package com.example.chessmate.util.chess

class PieceSquareTables {

    //player piece squares
    val playerPawns = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F),
        floatArrayOf(1.0F, 1.0F, 2.0F, 3.0F, 3.0F, 2.0F, 1.0F, 1.0F),
        floatArrayOf(0.5F, 0.5F, 1.0F, 2.5F, 2.5F, 1.0F, 0.5F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, -0.5F, -1.0F, 0.0F, 0.0F, -1.0F, -0.5F, 0.5F),
        floatArrayOf(0.5F, 1.0F, 1.0F, -2.0F, -2.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
    )

    val playerKnights = arrayOf(
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-3.0F, 0.0F, 1.0F, 1.5F, 1.5F, 1.0F, 0.0F, -3.0F),
        floatArrayOf(-3.0F, 0.5F, 1.5F, 2.0F, 2.0F, 1.5F, 0.5F, -3.0F),
        floatArrayOf(-3.0F, 0.0F, 1.5F, 2.0F, 2.0F, 1.5F, 0.0F, -3.0F),
        floatArrayOf(-3.0F, 0.5F, 1.0F, 1.5F, 1.5F, 1.0F, 0.5F, -3.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.5F, 0.5F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F)
    )

    val playerBishops = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F),
        floatArrayOf(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F)
    )

    val playerRooks = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F)
    )

    val playerQueen = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F),
        floatArrayOf(0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F),
        floatArrayOf(-1.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F)
    )

    val playerKingMidGame = arrayOf(
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-2.0F, -3.0F, -3.0F, -4.0F, -4.0F, -3.0F, -3.0F, -2.0F),
        floatArrayOf(-1.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -1.0F),
        floatArrayOf(2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F),
        floatArrayOf(2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 2.0F)
    )

    val playerKingEndGame = arrayOf(
        floatArrayOf(-5.0F, -4.0F, -3.0F, -2.0F, -2.0F, -3.0F, -4.0F, -5.0F),
        floatArrayOf(-3.0F, -2.0F, -1.0F, 0.0F, 0.0F, -1.0F, -2.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.0F, -3.0F),
        floatArrayOf(-5.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -5.0F)
    )

    //bot piece squares
    val botPawns = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, 1.0F, 1.0F, -2.0F, -2.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(0.5F, -0.5F, -1.0F, 0.0F, 0.0F, -1.0F, -0.5F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(0.5F, 0.5F, 1.0F, 2.5F, 2.5F, 1.0F, 0.5F, 0.5F),
        floatArrayOf(1.0F, 1.0F, 2.0F, 3.0F, 3.0F, 2.0F, 1.0F, 1.0F),
        floatArrayOf(5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
    )

    val botKnights = arrayOf(
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.5F, 0.5F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-3.0F, 0.5F, 1.0F, 1.5F, 1.5F, 1.0F, 0.5F, -3.0F),
        floatArrayOf(-3.0F, 0.0F, 1.5F, 2.0F, 2.0F, 1.5F, 0.0F, -3.0F),
        floatArrayOf(-3.0F, 0.5F, 1.5F, 2.0F, 2.0F, 1.5F, 0.5F, -3.0F),
        floatArrayOf(-3.0F, 0.0F, 1.0F, 1.5F, 1.5F, 1.0F, 0.0F, -3.0F),
        floatArrayOf(-4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0F, -4.0F),
        floatArrayOf(-5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F)
    )

    val botBishops = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, -1.0F),
        floatArrayOf(-1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F)
    )

    val botRooks = arrayOf(
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F),
        floatArrayOf(0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F),
        floatArrayOf(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
    )

    val botQueen = arrayOf(
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, -1.0F),
        floatArrayOf(-0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, 0.0F),
        floatArrayOf(-0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F),
        floatArrayOf(-1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F),
        floatArrayOf(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F),
        floatArrayOf(-2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F)
    )

    val botKingMidGame = arrayOf(
        floatArrayOf(2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 2.0F),
        floatArrayOf(2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F),
        floatArrayOf(-1.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -1.0F),
        floatArrayOf(-2.0F, -3.0F, -3.0F, -4.0F, -4.0F, -3.0F, -3.0F, -2.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F),
        floatArrayOf(-3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F)
    )

    val botKingEndGame = arrayOf(
        floatArrayOf(-5.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -5.0F),
        floatArrayOf(-3.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F),
        floatArrayOf(-3.0F, -2.0F, -1.0F, 0.0F, 0.0F, -1.0F, -2.0F, -3.0F),
        floatArrayOf(-5.0F, -4.0F, -3.0F, -2.0F, -2.0F, -3.0F, -4.0F, -5.0F)
    )
}