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
        imageView = null
    }

    fun dupe(source: Square) {
        pieceColor = source.pieceColor
        pieceType = source.pieceType
        isOccupied = source.isOccupied
        imageView = source.imageView
    }

    fun swap(other: Square){
        val tempIsOccupied = isOccupied
        val tempPieceColor = pieceColor
        val tempPieceType = pieceType

        isOccupied = other.isOccupied
        pieceColor = other.pieceColor
        pieceType = other.pieceType

        other.isOccupied = tempIsOccupied
        other.pieceColor = tempPieceColor
        other.pieceType = tempPieceType
    }

    fun move(other: Square){
        other.isOccupied = isOccupied
        other.pieceColor = pieceColor
        other.pieceType = pieceType

        isOccupied = false
        pieceColor = null
        pieceType = null
    }
}
