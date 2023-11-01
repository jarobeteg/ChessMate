package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R

class Pawn(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_cirlce"
    fun isValidMove(destinationSquare: Square): Boolean {
        if (!destinationSquare.isOccupied){
            if ( currentSquare.row == 6){
                if (currentSquare.row - 1 == destinationSquare.row && currentSquare.col == destinationSquare.col ||
                    currentSquare.row - 2 == destinationSquare.row && currentSquare.col == destinationSquare.col) {
                    return true
                }
            }else {
                if (currentSquare.row - 1 == destinationSquare.row && currentSquare.col == destinationSquare.col) {
                    return true
                }
            }
        }
        return false
    }

    fun showHighlightSquares(){
        if (currentSquare.row == 6){
            if (!chessboard.getSquare(currentSquare.row - 1, currentSquare.col).isOccupied) {
                addHighlightSquare(currentSquare.row - 1, currentSquare.col)
                if (!chessboard.getSquare(currentSquare.row - 1, currentSquare.col).isOccupied &&
                    !chessboard.getSquare(currentSquare.row - 2, currentSquare.col).isOccupied
                    ) {
                    addHighlightSquare(currentSquare.row - 2, currentSquare.col)
                }
            }
        }else {
            if (!chessboard.getSquare(currentSquare.row - 1, currentSquare.col).isOccupied) {
                addHighlightSquare(currentSquare.row - 1, currentSquare.col)
            }
        }
    }

    private fun addHighlightSquare(row: Int, col: Int) {
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
}