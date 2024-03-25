package com.example.chessmate.util.chess

import android.widget.FrameLayout
import android.widget.ImageView

data class Square(
    val row: Int,
    val col: Int,
    var isOccupied: Boolean,
    var pieceColor: PieceColor?,
    var pieceType: PieceType?,
    var hasMoved: Boolean = false,
    var frameLayout: FrameLayout? = null,
    var imageView: ImageView? = null
){

    fun copyWithoutUI(): Square{
        return Square(row, col, isOccupied, pieceColor, pieceType, hasMoved)
    }

    fun clearSquare(){
        isOccupied = false
        pieceColor = null
        pieceType = null
    }

    fun movePerformed(){
        hasMoved = true
    }

    fun moveReversed(){
        hasMoved = false
    }

    fun clearUI(){
        imageView = null
    }
}
