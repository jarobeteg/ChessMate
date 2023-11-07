package com.example.chessmate.util.chess

import android.widget.FrameLayout
import android.widget.ImageView

data class Square(
    val row: Int,
    val col: Int,
    var isOccupied: Boolean,
    var pieceColor: PieceColor?,
    var pieceType: PieceType?,
    var frameLayout: FrameLayout? = null,
    var imageView: ImageView? = null
){
    fun clearSquare(){
        isOccupied = false
        pieceColor = null
        pieceType = null
        imageView = null
    }

    fun dupe(source: Square) {
        pieceColor = source.pieceColor
        pieceType = source.pieceType
        isOccupied = source.isOccupied
        imageView = source.imageView
    }
}
