package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R

class Pawn(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
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
        }else{
            val leftDiagonalRow = currentSquare.row - 1
            val leftDiagonalCol = currentSquare.col - 1
            val rightDiagonalRow = currentSquare.row - 1
            val rightDiagonalCol = currentSquare.col + 1

            if (chessboard.getSquare(destinationSquare.row, destinationSquare.col).pieceColor != currentSquare.pieceColor &&
                destinationSquare.row == leftDiagonalRow && destinationSquare.col == leftDiagonalCol) {
                return true
            }

            if (chessboard.getSquare(destinationSquare.row, destinationSquare.col).pieceColor != currentSquare.pieceColor &&
                destinationSquare.row == rightDiagonalRow && destinationSquare.col == rightDiagonalCol) {
                return true
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
            if (chessboard.isValidSquare(currentSquare.row - 1, currentSquare.col) && !chessboard.getSquare(currentSquare.row - 1, currentSquare.col).isOccupied) {
                addHighlightSquare(currentSquare.row - 1, currentSquare.col)
            }
            val leftDiagonalRow = currentSquare.row - 1
            val leftDiagonalCol = currentSquare.col - 1
            val rightDiagonalRow = currentSquare.row - 1
            val rightDiagonalCol = currentSquare.col + 1

            if (chessboard.isValidSquare(leftDiagonalRow, leftDiagonalCol) &&
                chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).isOccupied &&
                chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).pieceColor != currentSquare.pieceColor) {
                addHighlightOpponent(leftDiagonalRow, leftDiagonalCol)
            }

            if (chessboard.isValidSquare(rightDiagonalRow, rightDiagonalCol) &&
                chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).isOccupied &&
                chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).pieceColor != currentSquare.pieceColor) {
                addHighlightOpponent(rightDiagonalRow, rightDiagonalCol)
            }
        }
    }

    fun canCheckBeBlocked(): Boolean{
        TODO("Not yet implemented")
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

    private fun addHighlightOpponent(row: Int, col: Int) {
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