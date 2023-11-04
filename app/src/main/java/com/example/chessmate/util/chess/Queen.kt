package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R
import kotlin.math.abs

class Queen(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    fun isValidMove(destinationSquare: Square): Boolean {
        val rowDiff = abs(destinationSquare.row - currentSquare.row)
        val colDiff = abs(destinationSquare.col - currentSquare.col)

        if (rowDiff == 0 || colDiff == 0 || rowDiff == colDiff) {
            val rowIncrement = if (destinationSquare.row > currentSquare.row) 1 else if (destinationSquare.row < currentSquare.row) -1 else 0
            val colIncrement = if (destinationSquare.col > currentSquare.col) 1 else if (destinationSquare.col < currentSquare.col) -1 else 0

            var rowToCheck = currentSquare.row + rowIncrement
            var colToCheck = currentSquare.col + colIncrement

            while (rowToCheck != destinationSquare.row || colToCheck != destinationSquare.col) {
                val squareToCheck = chessboard.getSquare(rowToCheck, colToCheck)
                if (squareToCheck.isOccupied) {
                    return false
                }
                rowToCheck += rowIncrement
                colToCheck += colIncrement
            }
            return true
        }

        return false
    }


    fun showHighlightSquare(){
        bishopHighlightMoves()
        rookHighlightMoves()
    }

    private fun bishopHighlightMoves(){
        val row = currentSquare.row
        val col = currentSquare.col

        var r = row - 1
        var c = col + 1
        while (r >= 0 && c < 8) {
            if (isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, c)
                    }
                    break
                }else{
                    addHighlightSquare(r, c)
                }
            } else {
                break
            }
            r--
            c++
        }

        r = row + 1
        c = col - 1
        while (r < 8 && c >= 0) {
            if (isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, c)
                    }
                    break
                }else{
                    addHighlightSquare(r, c)
                }
            } else {
                break
            }
            r++
            c--
        }

        r = row - 1
        c = col - 1
        while (r >= 0 && c >= 0) {
            if (isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, c)
                    }
                    break
                }else{
                    addHighlightSquare(r, c)
                }
            } else {
                break
            }
            r--
            c--
        }

        r = row + 1
        c = col + 1
        while (r < 8 && c < 8) {
            if (isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        addHighlightOpponent(r, c)
                    }
                    break
                }else{
                    addHighlightSquare(r, c)
                }
            } else {
                break
            }
            r++
            c++
        }
    }

    private fun rookHighlightMoves(){
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