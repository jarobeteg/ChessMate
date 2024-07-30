package com.example.chessmate.util.chess

class PestoEvalTables {
    private val midGameValue = floatArrayOf(8.2F, 33.7F, 36.5F, 47.7F, 102.5F, 0.0F)
    private val endGameValue = floatArrayOf(9.4F, 28.1F, 29.7F, 51.2F, 93.6F, 0.0F)

    private val midGamePawnTable = floatArrayOf(
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
        -3.5F,  -0.1F, -2.0F, -2.3F, -1.5F,  2.4F, 3.8F, -2.2F,
        -2.6F,  -0.4F,  -0.4F, -1.0F,   0.3F,   0.3F, 3.3F, -1.2F,
        -2.7F,  -0.2F,  -0.5F,  1.2F,  1.7F,   0.6F, 1.0F, -2.5F,
        -1.4F,  1.3F,   0.6F,  2.1F,  2.3F,  1.2F, 1.7F, -2.3F,
        -0.6F,   0.7F,  2.6F,  3.1F,  6.5F,  5.6F, 2.5F, -2.0F,
        9.8F, 13.4F,  6.1F,  9.5F,  6.8F, 12.6F, 3.4F, -1.1F,
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
    )

    private val endGamePawnTable = floatArrayOf(
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
        1.3F, 0.8F, 0.8F, 1.0F, 1.3F, 0.0F, 0.2F, -0.7F,
        0.4F, 0.7F, -0.6F, 0.1F, 0.0F, -0.5F, -0.1F, -0.8F,
        1.3F, 0.9F, -0.3F, -0.7F, -0.7F, -0.8F, 0.3F, -0.1F,
        3.2F, 2.4F, 1.3F, 0.5F, -0.2F, 0.4F, 1.7F, 1.7F,
        9.4F, 10.0F, 8.5F, 6.7F, 5.6F, 5.3F, 8.2F, 8.4F,
        17.8F, 17.3F, 15.8F, 13.4F, 14.7F, 13.2F, 16.5F, 18.7F,
        0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
    )

    private val midGameKnightTable = floatArrayOf(
        -10.5F, -2.1F, -5.8F, -3.3F, -1.7F, -2.8F, -1.9F, -2.3F,
        -4.2F, -2.0F, -1.0F, -0.5F, -0.2F, -2.0F, -2.3F, -4.4F,
        -2.3F, -0.3F, -0.1F, 1.5F, 1.0F, -0.3F, -2.0F, -2.2F,
        -1.8F, -0.6F, 1.6F, 2.5F, 1.6F, 1.7F, 0.4F, -1.8F,
        -1.7F,  0.3F, 2.2F,  2.2F,  2.2F, 1.1F, 0.8F, -1.8F,
        -2.4F, -2.0F, 1.0F, 0.9F, -0.1F, -0.9F, -1.9F, -4.1F,
        -2.5F, -0.8F, -2.5F, -0.2F, -0.9F, -2.5F, -2.4F, -5.2F,
        -5.8F, -3.8F, -1.3F, -2.8F, -3.1F, -2.7F, -6.3F, -9.9F
    )

    private val endGameKnightTable = floatArrayOf(
        -2.9F, -5.1F, -2.3F, -1.5F, -2.2F, -1.8F, -5.0F, -6.4F,
        -4.2F, -2.0F, -1.0F, -0.5F, -0.2F, -2.0F, -2.3F, -4.4F,
        -2.3F, -0.3F, -0.1F, 1.5F, 1.0F, -0.3F, -2.0F, -2.2F,
        -1.8F, -0.6F, 1.6F, 2.5F, 1.6F, 1.7F, 0.4F, -1.8F,
        -1.7F, 0.3F, 2.2F, 2.2F, 2.2F, 1.1F, 0.8F, -1.8F,
        -2.4F, -2.0F, 1.0F, 0.9F, -0.1F, -0.9F, -1.9F, -4.1F,
        -2.5F, -0.8F, -2.5F, -0.2F, -0.9F, -2.5F, -2.4F, -5.2F,
        -5.8F, -3.8F, -1.3F, -2.8F, -3.1F, -2.7F, -6.3F, -9.9F,
    )

    private val midGameBishopTable = floatArrayOf(
        -3.3F, -0.3F, -1.4F, -2.1F, -1.3F, -1.2F, -3.9F, -2.1F,
        0.4F, 1.5F, 1.6F, 0.0F, 0.7F, 2.1F, 3.3F, 0.1F,
        0.0F, 1.5F, 1.5F, 1.5F, 1.4F, 2.7F, 1.8F, 1.0F,
        -0.6F, 1.3F, 1.3F, 2.6F, 3.4F, 1.2F, 1.0F, 0.4F,
        -0.4F, 0.5F, 1.9F, 5.0F, 3.7F, 3.7F, 0.7F, -0.2F,
        -1.6F, 3.7F, 4.3F, 4.0F, 3.5F, 5.0F, 3.7F, -0.2F,
        -2.6F, 1.6F, -1.8F, -1.3F, 3.0F, 5.9F, 1.8F, -4.7F,
        -2.9F, 0.4F, -8.2F, -3.7F, -2.5F, -4.2F, 0.7F, -0.8F,
    )

    private val endGameBishopTable = floatArrayOf(
        -2.3F, -0.9F, -2.3F, -0.5F, -0.9F, -1.6F, -0.5F, -1.7F,
        -1.4F, -1.8F, -0.7F, -0.1F, 0.4F, -0.9F, -1.5F, -2.7F,
        -1.2F, -0.3F, 0.8F, 1.0F, 1.3F, 0.3F, -0.7F, -1.5F,
        -0.6F, 0.3F, 1.3F, 1.9F, 0.7F, 1.0F, -0.3F, -0.9F,
        -0.3F, 0.9F, 1.2F, 0.9F, 1.4F, 1.0F, 0.3F, 0.2F,
        0.2F, -0.8F, 0.0F, -0.1F, -0.2F, 0.6F, 0.0F, 0.4F,
        -0.8F, -0.4F, 0.7F, -1.2F, -0.3F, -1.3F, -0.4F, -1.4F,
        -1.4F, -2.1F, -1.1F, -0.8F, -0.7F, -0.9F, -1.7F, -2.4F,
    )

    private val midGameRookTable = floatArrayOf(
        -1.9F, -1.3F, 0.1F, 1.7F, 1.6F, 0.7F, -3.7F, -2.6F,
        -4.4F, -1.6F, -2.0F, -0.9F, -0.1F, 1.1F, -0.6F, -7.1F,
        -4.5F, -2.5F, -1.6F, -1.7F, 0.3F, 0.0F, -0.5F, -3.3F,
        -3.6F, -2.6F, -1.2F, -0.1F, 0.9F, -0.7F, 0.6F, -2.3F,
        -2.4F, -1.1F, 0.7F, 2.6F, 2.4F, 3.5F, -0.8F, -2.0F,
        -0.5F, 1.9F, 2.6F, 3.6F, 1.7F, 4.5F, 6.1F, 1.6F,
        2.7F, 3.2F, 5.8F, 6.2F, 8.0F, 6.7F, 2.6F, 4.4F,
        3.2F, 4.2F, 3.2F, 5.1F, 6.3F, 0.9F, 3.1F, 4.3F,
    )

    private val endGameRookTable = floatArrayOf(
        -0.9F, 0.2F, 0.3F, -0.1F, -0.5F, -1.3F, 0.4F, -2.0F,
        -0.6F, -0.6F, 0.0F, 0.2F, -0.9F, -0.9F, -1.1F, -0.3F,
        -0.4F, 0.0F, -0.5F, -0.1F, -0.7F, -1.2F, -0.8F, -1.6F,
        0.3F, 0.5F, 0.8F, 0.4F, -0.5F, -0.6F, -0.8F, -1.1F,
        0.4F, 0.3F, 1.3F, 0.1F, 0.2F, 0.1F, -0.1F, 0.2F,
        0.7F, 0.7F, 0.7F, 0.5F, 0.4F, -0.3F, -0.5F, -0.3F,
        1.1F, 1.3F, 1.3F, 1.1F, -0.3F, 0.3F, 0.8F, 0.3F,
        1.3F, 1.0F, 1.8F, 1.5F, 1.2F, 1.2F, 0.8F, 0.5F,
    )

    private val midGameQueenTable = floatArrayOf(
        -0.1F, -1.8F, -0.9F, 1.0F, -1.5F, -2.5F, -3.1F, -5.0F,
        -3.5F, -0.8F, 1.1F, 0.2F, 0.8F, 1.5F, -0.3F, 0.1F,
        -1.4F, 0.2F, -1.1F, -0.2F, -0.5F, 0.2F, 1.4F, 0.5F,
        -0.9F, -2.6F, -0.9F, -1.0F, -0.2F, -0.4F, 0.3F, -0.3F,
        -2.7F, -2.7F, -1.6F, -1.6F, -0.1F, 1.7F, -0.2F, 0.1F,
        -1.3F, -1.7F, 0.7F, 0.8F, 2.9F, 5.6F, 4.7F, 5.7F,
        -2.4F, -3.9F, -0.5F, 0.1F, -1.6F, 5.7F, 2.8F, 5.4F,
        -2.8F, 0.0F, 2.9F, 1.2F, 5.9F, 4.4F, 4.3F, 4.5F,
    )

    private val endGameQueenTable = floatArrayOf(
        -3.3F, -2.8F, -2.2F, -4.3F, -0.5F, -3.2F, -2.0F, -4.1F,
        -2.2F, -2.3F, -3.0F, -1.6F, -1.6F, -2.3F, -3.6F, -3.2F,
        -1.6F, -2.7F, 1.5F, 0.6F, 0.9F, 1.7F, 1.0F, 0.5F,
        -1.8F, 2.8F, 1.9F, 4.7F, 3.1F, 3.4F, 3.9F, 2.3F,
        0.3F, 2.2F, 2.4F, 4.5F, 5.7F, 4.0F, 5.7F, 3.6F,
        -2.0F, 0.6F, 0.9F, 4.9F, 4.7F, 3.5F, 1.9F, 0.9F,
        -1.7F, 2.0F, 3.2F, 4.1F, 5.8F, 2.5F, 3.0F, 0.0F,
        -0.9F, 2.2F, 2.2F, 2.7F, 2.7F, 1.9F, 1.0F, 2.0F,
    )

    private val midGameKingTable = floatArrayOf(
        -1.5F, 3.6F, 1.2F, -5.4F, 0.8F, -2.8F, 2.4F, 1.4F,
        0.1F, 0.7F, -0.8F, -6.4F, -4.3F, -1.6F, 0.9F, 0.8F,
        -1.4F, -1.4F, -2.2F, -4.6F, -4.4F, -3.0F, -1.5F, -2.7F,
        -4.9F, -0.1F, -2.7F, -3.9F, -4.6F, -4.4F, -3.3F, -5.1F,
        -1.7F, -2.0F, -1.2F, -2.7F, -3.0F, -2.5F, -1.4F, -3.6F,
        -0.9F, 2.4F, 0.2F, -1.6F, -2.0F, 0.6F, 2.2F, -2.2F,
        2.9F, -0.1F, -2.0F, -0.7F, -0.8F, -0.4F, -3.8F, -2.9F,
        -6.5F, 2.3F, 1.6F, -1.5F, -5.6F, -3.4F, 0.2F, 1.3F,
    )

    private val endGameKingTable = floatArrayOf(
        -5.3F, -3.4F, -2.1F, -1.1F, -2.8F, -1.4F, -2.4F, -4.3F,
        -2.7F, -1.1F, 0.4F, 1.3F, 1.4F, 0.4F, -0.5F, -1.7F,
        -1.9F, -0.3F, 1.1F, 2.1F, 2.3F, 1.6F, 0.7F, -0.9F,
        -1.8F, -0.4F, 2.1F, 2.4F, 2.7F, 2.3F, 0.9F, -1.1F,
        -0.8F, 2.2F, 2.4F, 2.7F, 2.6F, 3.3F, 2.6F, 0.3F,
        1.0F, 1.7F, 2.3F, 1.5F, 2.0F, 4.5F, 4.4F, 1.3F,
        -1.2F, 1.7F, 1.4F, 1.7F, 1.7F, 3.8F, 2.3F, 1.1F,
        -7.4F, -3.5F, -1.8F, -1.8F, -1.1F, 1.5F, 0.4F, -1.7F,
    )

    private fun mirrorArray(array: FloatArray): FloatArray {
        val mirroredArray = FloatArray(64)

        for (i in 0 until 8) {
            for (j in 0 until 8) {
                mirroredArray[i * 8 + j] = array[(7 - i) * 8 + j]
            }
        }

        return mirroredArray
    }

    private fun getValue(table: FloatArray, idx: Int): Float {
        return table[idx]
    }

    fun getMidGamePawnValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGamePawnTable) else midGamePawnTable, idx) + midGameValue[0]
        return score
    }

    fun getEndGamePawnValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGamePawnTable) else endGamePawnTable, idx) + endGameValue[0]
        return score
    }

    fun getMidGameKnightValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGameKnightTable) else midGameKnightTable, idx) + midGameValue[1]
        return score
    }

    fun getEndGameKnightValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGameKnightTable) else endGameKnightTable, idx) + endGameValue[1]
        return score
    }

    fun getMidGameBishopValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGameBishopTable) else midGameBishopTable, idx) + midGameValue[2]
        return score
    }

    fun getEndGameBishopValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGameBishopTable) else endGameBishopTable, idx) + endGameValue[2]
        return score
    }

    fun getMidGameRookValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGameRookTable) else midGameRookTable, idx) + midGameValue[3]
        return score
    }

    fun getEndGameRookValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGameRookTable) else endGameRookTable, idx) + endGameValue[3]
        return score
    }

    fun getMidGameQueenValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGameQueenTable) else midGameQueenTable, idx) + midGameValue[4]
        return score
    }

    fun getEndGameQueenValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGameQueenTable) else endGameQueenTable, idx) + endGameValue[4]
        return score
    }

    fun getMidGameKingValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(midGameKingTable) else midGameKingTable, idx) + midGameValue[5]
        return score
    }

    fun getEndGameKingValue(idx: Int, isMirrored: Boolean): Float {
        val score = getValue(if (isMirrored) mirrorArray(endGameKingTable) else endGameKingTable, idx) + endGameValue[5]
        return score
    }
}