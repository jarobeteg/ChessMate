package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R
import kotlin.math.abs

class King(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    fun isValidMove(destinationSquare: Square): Boolean{
        val rowDiff = abs(destinationSquare.row - currentSquare.row)
        val colDiff = abs(destinationSquare.col - currentSquare.col)

        if (chessboard.isKingInCheck(chessboard, destinationSquare, currentSquare.pieceColor!!)) return false
        return (rowDiff <= 1 && colDiff <= 1)
    }

    fun isCastlingMoveValid(isKingSideCastles: Boolean, isPlayerWhite: Boolean): Boolean{
        val kingPosition = if (isPlayerWhite) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        val kingSideRook = if (isPlayerWhite) chessboard.getWhiteKingSideRook() else chessboard.getBlackKingSideRook()
        val queenSideRook = if (isPlayerWhite) chessboard.getWhiteQueenSideRook() else chessboard.getBlackQueenSideRook()
        if (isPlayerWhite) {
            if (isKingSideCastles) {
                if (!kingSideRook.hasMoved && kingPosition?.hasMoved == false) {
                    val kingSideSquareOne = chessboard.getSquare(7, 5)
                    val kingSideSquareTwo = chessboard.getSquare(7, 6)
                    if (!kingSideSquareOne.isOccupied && !kingSideSquareTwo.isOccupied) {
                        if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, kingSideSquareOne, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, kingSideSquareTwo, kingPosition.pieceColor!!)) {
                            return true
                        }
                    }
                }
            }else {
                if (!queenSideRook.hasMoved && kingPosition?.hasMoved == false) {
                    val queenSideSquareOne = chessboard.getSquare(7, 3)
                    val queenSideSquareTwo = chessboard.getSquare(7, 2)
                    val queenSideSquareThree = chessboard.getSquare(7, 1)
                    if (!queenSideSquareOne.isOccupied && !queenSideSquareTwo.isOccupied && !queenSideSquareThree.isOccupied) {
                        if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, queenSideSquareOne, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, queenSideSquareTwo, kingPosition.pieceColor!!)) {
                            return true
                        }
                    }
                }
            }
        }else {
            if (isKingSideCastles) {
                if (!kingSideRook.hasMoved && kingPosition?.hasMoved == false) {
                    val kingSideSquareOne = chessboard.getSquare(7, 2)
                    val kingSideSquareTwo = chessboard.getSquare(7, 1)
                    if (!kingSideSquareOne.isOccupied && !kingSideSquareTwo.isOccupied) {
                        if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, kingSideSquareOne, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, kingSideSquareTwo, kingPosition.pieceColor!!)) {
                            return true
                        }
                    }
                }
            } else {
                if (!queenSideRook.hasMoved && kingPosition?.hasMoved == false) {
                    val queenSideSquareOne = chessboard.getSquare(7, 4)
                    val queenSideSquareTwo = chessboard.getSquare(7, 5)
                    val queenSideSquareThree = chessboard.getSquare(7, 6)
                    if (!queenSideSquareOne.isOccupied && !queenSideSquareTwo.isOccupied && !queenSideSquareThree.isOccupied) {
                        if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, queenSideSquareOne, kingPosition.pieceColor!!) &&
                            !chessboard.isKingInCheck(chessboard, queenSideSquareTwo, kingPosition.pieceColor!!)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun showHighlightSquares() {
        val row = currentSquare.row
        val col = currentSquare.col

        for (r in -1..1) {
            for (c in -1..1) {
                if (r == 0 && c == 0) {
                    continue
                }

                val newRow = row + r
                val newCol = col + c

                if (isValidSquare(newRow, newCol)) {
                    val destSquare = chessboard.getSquare(newRow, newCol)
                    if (chessboard.getSquare(newRow, newCol).isOccupied){
                        if (chessboard.getSquare(newRow, newCol).pieceColor != currentSquare.pieceColor){
                            if (!chessboard.isKingInCheck(chessboard, destSquare, currentSquare.pieceColor!!)) {
                                addHighlightOpponent(newRow, newCol)
                            }
                        }
                    }else{
                        if (!chessboard.isKingInCheck(chessboard, destSquare, currentSquare.pieceColor!!)) {
                            addHighlightSquare(newRow, newCol)
                        }
                    }
                }
            }
        }
    }

    fun showIfCastlingPossible() {
        val isPlayerWhite = currentSquare.pieceColor == PieceColor.WHITE
        val kingPosition = if (isPlayerWhite) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        val kingSideRook = if (isPlayerWhite) chessboard.getWhiteKingSideRook() else chessboard.getBlackKingSideRook()
        val queenSideRook = if (isPlayerWhite) chessboard.getWhiteQueenSideRook() else chessboard.getBlackQueenSideRook()
        if (isPlayerWhite) {
            if (!kingSideRook.hasMoved && kingPosition?.hasMoved == false) {
                val kingSideSquareOne = chessboard.getSquare(7, 5)
                val kingSideSquareTwo = chessboard.getSquare(7, 6)
                if (!kingSideSquareOne.isOccupied && !kingSideSquareTwo.isOccupied) {
                    if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, kingSideSquareOne, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, kingSideSquareTwo, kingPosition.pieceColor!!)) {
                        addHighlightSquare(7, 6)
                    }
                }
            }
            if (!queenSideRook.hasMoved && kingPosition?.hasMoved == false) {
                val queenSideSquareOne = chessboard.getSquare(7,3)
                val queenSideSquareTwo = chessboard.getSquare(7,2)
                val queenSideSquareThree = chessboard.getSquare(7,1)
                if (!queenSideSquareOne.isOccupied && !queenSideSquareTwo.isOccupied && !queenSideSquareThree.isOccupied){
                    if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, queenSideSquareOne, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, queenSideSquareTwo, kingPosition.pieceColor!!)){
                        addHighlightSquare(7,2)
                    }
                }
            }
        }else{
            if (!kingSideRook.hasMoved && kingPosition?.hasMoved == false) {
                val kingSideSquareOne = chessboard.getSquare(7, 2)
                val kingSideSquareTwo = chessboard.getSquare(7, 1)
                if (!kingSideSquareOne.isOccupied && !kingSideSquareTwo.isOccupied) {
                    if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, kingSideSquareOne, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, kingSideSquareTwo, kingPosition.pieceColor!!)) {
                        addHighlightSquare(7, 1)
                    }
                }
            }
            if (!queenSideRook.hasMoved && kingPosition?.hasMoved == false) {
                val queenSideSquareOne = chessboard.getSquare(7,4)
                val queenSideSquareTwo = chessboard.getSquare(7,5)
                val queenSideSquareThree = chessboard.getSquare(7,6)
                if (!queenSideSquareOne.isOccupied && !queenSideSquareTwo.isOccupied && !queenSideSquareThree.isOccupied){
                    if (!chessboard.isKingInCheck(chessboard, kingPosition, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, queenSideSquareOne, kingPosition.pieceColor!!) &&
                        !chessboard.isKingInCheck(chessboard, queenSideSquareTwo, kingPosition.pieceColor!!)){
                        addHighlightSquare(7,5)
                    }
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