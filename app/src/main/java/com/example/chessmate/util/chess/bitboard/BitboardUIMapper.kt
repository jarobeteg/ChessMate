package com.example.chessmate.util.chess.bitboard

import android.widget.FrameLayout

class BitboardUIMapper {
    private val positionToSquareMap = mutableMapOf<Long, FrameLayout>()

    fun addSquare(position: Long, squareView: FrameLayout) {
        positionToSquareMap[position] = squareView
    }

    fun getSquareView(position: Long): FrameLayout {
        return positionToSquareMap[position] ?: throw IllegalArgumentException("Invalid position: $position")
    }
}