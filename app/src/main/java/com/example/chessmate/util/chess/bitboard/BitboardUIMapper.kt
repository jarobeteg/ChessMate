package com.example.chessmate.util.chess.bitboard

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import com.example.chessmate.R

class BitboardUIMapper (private val context: Context) {
    private val positionToSquareMap = mutableMapOf<Long, FrameLayout>()

    fun addSquare(position: Long, squareView: FrameLayout) {
        positionToSquareMap[position] = squareView
    }

    fun getSquareView(position: Long): FrameLayout {
        return positionToSquareMap[position] ?: throw IllegalArgumentException("Invalid position: $position")
    }
}