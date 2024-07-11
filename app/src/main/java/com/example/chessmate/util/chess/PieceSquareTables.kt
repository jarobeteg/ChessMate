package com.example.chessmate.util.chess

class PieceSquareTables {
    private val pawnTable = floatArrayOf(
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
        0.5F, 1.0F, 1.0F, -2.0F, -2.0F, 1.0F, 1.0F, 0.5F,
        0.5F, -0.5F, -1.0F, 0.0F, 0.0F, -1.0F, -0.5F, 0.5F,
        0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F,
        0.5F, 0.5F, 1.0F, 2.5F, 2.5F, 1.0F, 0.5F, 0.5F,
        1.0F, 1.0F, 2.0F, 3.0F, 3.0F, 2.0F, 1.0F, 1.0F,
        5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F,
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
    )

    private val knightTable = floatArrayOf(
        -5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F,
        -4.0F, -2.0F, 0.0F, 0.5F, 0.5F, 0.0F, -2.0F, -4.0F,
        -3.0F, 0.5F, 1.0F, 1.5F, 1.5F, 1.0F, 0.5F, -3.0F,
        -3.0F, 0.0F, 1.5F, 2.0F, 2.0F, 1.5F, 0.0F, -3.0F,
        -3.0F, 0.5F, 1.5F, 2.0F, 2.0F, 1.5F, 0.5F, -3.0F,
        -3.0F, 0.0F, 1.0F, 1.5F, 1.5F, 1.0F, 0.0F, -3.0F,
        -4.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.0F, -4.0F,
        -5.0F, -4.0F, -3.0F, -3.0F, -3.0F, -3.0F, -4.0F, -5.0F
    )

    private val bishopTable = floatArrayOf(
        -2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F,
        -1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, -1.0F,
        -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F,
        -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, -1.0F,
        -1.0F, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, -1.0F,
        -1.0F, 0.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.0F, -1.0F,
        -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F,
        -2.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -2.0F
    )

    private val rookTable = floatArrayOf(
        0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F,
        -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F,
        -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F,
        -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F,
        -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F,
        -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5F,
        0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F,
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
    )

    private val queenTable = floatArrayOf(
        -2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F,
        -1.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F,
        -1.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F,
        0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F,
        -0.5F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -0.5F,
        -1.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, -1.0F,
        -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F,
        -2.0F, -1.0F, -1.0F, -0.5F, -0.5F, -1.0F, -1.0F, -2.0F
    )

    private val kingMidGameTable = floatArrayOf(
        2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 2.0F,
        2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F,
        -1.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -2.0F, -1.0F,
        -2.0F, -3.0F, -3.0F, -4.0F, -4.0F, -3.0F, -3.0F, -2.0F,
        -3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F,
        -3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F,
        -3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F,
        -3.0F, -4.0F, -4.0F, -5.0F, -5.0F, -4.0F, -4.0F, -3.0F,
    )

    private val kingEndGameTable = floatArrayOf(
        -5.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -3.0F, -5.0F,
        -3.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.0F, -3.0F,
        -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F,
        -3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F,
        -3.0F, -1.0F, 3.0F, 4.0F, 4.0F, 3.0F, -1.0F, -3.0F,
        -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, 2.0F, -1.0F, -3.0F,
        -3.0F, -2.0F, -1.0F, 0.0F, 0.0F, -1.0F, -2.0F, -3.0F,
        -5.0F, -4.0F, -3.0F, -2.0F, -2.0F, -3.0F, -4.0F, -5.0F,
    )

    private fun getValue(table: FloatArray, idx: Int): Float {
        return table[idx]
    }

    fun getPawnValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(pawnTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getKnightValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(knightTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getBishopValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(bishopTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getRookValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(rookTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getQueenValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(queenTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getKingMidGameValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(kingMidGameTable, if (isMirrored) (63 - idx) else idx)
    }

    fun getKingEndGameValue(idx: Int, isMirrored: Boolean): Float {
        return getValue(kingEndGameTable, if (isMirrored) (63 - idx) else idx)
    }
}