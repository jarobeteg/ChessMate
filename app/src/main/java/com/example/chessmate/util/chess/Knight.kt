package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R
import kotlin.math.abs

class Knight(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    fun isValidMove(destinationSquare: Square): Boolean{
        val rowDiff = abs(destinationSquare.row - currentSquare.row)
        val colDiff = abs(destinationSquare.col - currentSquare.col)

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
    }

    fun showHighlightSquare(){
        val knightMoves = arrayOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )

        for (move in knightMoves) {
            val r = currentSquare.row + move.first
            val c = currentSquare.col + move.second

            if (isValidSquare(r, c) && (!chessboard.getSquare(r, c).isOccupied ||
                        chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor)){
                if (chessboard.getSquare(r, c).isOccupied &&
                    chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor){
                    addHighlightOpponent(r, c)
                }else{
                    addHighlightSquare(r, c)
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