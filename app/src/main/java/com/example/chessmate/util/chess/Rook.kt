package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R

class Rook(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    fun isValidMove(destinationSquare: Square): Boolean{
        val row = currentSquare.row
        val col = currentSquare.col
        if (destinationSquare.row == row || destinationSquare.col == col) {
            if (destinationSquare.col == col) {
                val start = destinationSquare.row.coerceAtMost(row) + 1
                val end = destinationSquare.row.coerceAtLeast(row)
                for (r in start until end) {
                    if (chessboard.getSquare(r, col).isOccupied) {
                        return false
                    }
                }
            }
            else if (destinationSquare.row == row) {
                val start = destinationSquare.col.coerceAtMost(col) + 1
                val end = destinationSquare.col.coerceAtLeast(col)
                for (c in start until end) {
                    if (chessboard.getSquare(row, c).isOccupied) {
                        return false
                    }
                }
            }
            if (!destinationSquare.isOccupied) {
                return true
            } else {
                return true
            }
        }

        return false
    }

    fun showHighlightSquare(){
        val row = currentSquare.row
        val col = currentSquare.col
        for (r in row - 1 downTo 0){
            if (isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, currentSquare.col)
                    }
                    break
                } else {
                    addHighlightSquare(r, currentSquare.col)
                }
            }
        }

        for (r in row + 1 until 8){
            if (isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, currentSquare.col)
                    }
                    break
                } else {
                    addHighlightSquare(r, currentSquare.col)
                }
            }
        }

        for (c in col - 1 downTo  0){
            if (isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(currentSquare.row, c)
                    }
                    break
                } else {
                    addHighlightSquare(currentSquare.row, c)
                }
            }
        }

        for (c in col + 1 until 8){
            if (isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(currentSquare.row, c)
                    }
                    break
                } else {
                    addHighlightSquare(currentSquare.row, c)
                }
            }
        }
    }

    private fun isValidSquare(row: Int, col: Int): Boolean{
        return row in 0 until 8 && col in 0 until 8
    }

    private fun addHighlightSquare(row: Int, col: Int){
        val frameLayout = FrameLayout(context)
        val squareSize = chessboardLayout.width / 8
        val circleSize = squareSize / 3

        val params = GridLayout.LayoutParams().apply {
            width = squareSize
            height = squareSize
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(col)
        }

        val circleParams = FrameLayout.LayoutParams(circleSize, circleSize).apply {
            gravity = Gravity.CENTER
        }

        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.highlight_square_circle)

        frameLayout.addView(imageView, circleParams)
        frameLayout.tag = highlightCircleTag

        chessboardLayout.addView(frameLayout, params)
    }

    private fun addHighlightOpponent(row: Int, col: Int){
        val square = chessboard.getSquare(row, col)
        val squareFrameLayout = square.frameLayout
        val squareImageView = square.imageView
        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag
        squareFrameLayout?.removeView(squareImageView)
        squareFrameLayout?.addView(imageView)
        squareFrameLayout?.addView(squareImageView)
    }
}