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
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        if (!destinationSquare.isOccupied){
            if (currentSquare.row - 1 == destinationSquare.row && currentSquare.col == destinationSquare.col) {
                destinationSquare.dupe(currentSquare)
                currentSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
                    currentSquare.dupe(destinationSquare)
                    destinationSquare.clearSquare()
                    return true
                }
                currentSquare.dupe(destinationSquare)
                destinationSquare.clearSquare()
            }
            if (currentSquare.row - 2 == destinationSquare.row && currentSquare.col == destinationSquare.col && currentSquare.row == 6 &&
                !chessboard.getSquare(currentSquare.row - 1, currentSquare.col).isOccupied) {
                destinationSquare.dupe(currentSquare)
                currentSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
                    currentSquare.dupe(destinationSquare)
                    destinationSquare.clearSquare()
                    return true
                }
                currentSquare.dupe(destinationSquare)
                destinationSquare.clearSquare()
            }
        }else{
            val leftDiagonalRow = currentSquare.row - 1
            val leftDiagonalCol = currentSquare.col - 1
            val rightDiagonalRow = currentSquare.row - 1
            val rightDiagonalCol = currentSquare.col + 1

            if (chessboard.getSquare(destinationSquare.row, destinationSquare.col).pieceColor != currentSquare.pieceColor &&
                destinationSquare.row == leftDiagonalRow && destinationSquare.col == leftDiagonalCol) {
                val tmpIsOccupied = destinationSquare.isOccupied
                val tmpPieceType = destinationSquare.pieceType
                val tmpPieceColor = destinationSquare.pieceColor
                val tmpImageView = destinationSquare.imageView
                destinationSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                    destinationSquare.pieceColor = tmpPieceColor
                    destinationSquare.pieceType = tmpPieceType
                    destinationSquare.isOccupied = tmpIsOccupied
                    destinationSquare.imageView = tmpImageView
                    return true
                }
                destinationSquare.pieceColor = tmpPieceColor
                destinationSquare.pieceType = tmpPieceType
                destinationSquare.isOccupied = tmpIsOccupied
                destinationSquare.imageView = tmpImageView
            }

            if (chessboard.getSquare(destinationSquare.row, destinationSquare.col).pieceColor != currentSquare.pieceColor &&
                destinationSquare.row == rightDiagonalRow && destinationSquare.col == rightDiagonalCol) {
                val tmpIsOccupied = destinationSquare.isOccupied
                val tmpPieceType = destinationSquare.pieceType
                val tmpPieceColor = destinationSquare.pieceColor
                val tmpImageView = destinationSquare.imageView
                destinationSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                    destinationSquare.pieceColor = tmpPieceColor
                    destinationSquare.pieceType = tmpPieceType
                    destinationSquare.isOccupied = tmpIsOccupied
                    destinationSquare.imageView = tmpImageView
                    return true
                }
                destinationSquare.pieceColor = tmpPieceColor
                destinationSquare.pieceType = tmpPieceType
                destinationSquare.isOccupied = tmpIsOccupied
                destinationSquare.imageView = tmpImageView
            }
        }
        return false
    }

    fun showHighlightSquares(){
        val newRow = currentSquare.row - 1
        if (chessboard.isValidSquare(newRow, currentSquare.col) && !chessboard.getSquare(newRow, currentSquare.col).isOccupied) {
            addHighlightSquare(newRow, currentSquare.col)

            if (currentSquare.row == 6) {
                val doubleMoveRow = currentSquare.row - 2
                if (!chessboard.getSquare(doubleMoveRow, currentSquare.col).isOccupied) {
                    addHighlightSquare(doubleMoveRow, currentSquare.col)
                }
            }
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

    fun canCheckBeBlocked(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        val newRow = currentSquare.row - 1
        if (chessboard.isValidSquare(newRow, currentSquare.col) && !chessboard.getSquare(newRow, currentSquare.col).isOccupied) {
            var destSquare = chessboard.getSquare(newRow, currentSquare.col)
            destSquare.dupe(currentSquare)
            currentSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
                currentSquare.dupe(destSquare)
                destSquare.clearSquare()
                addHighlightSquare(newRow, currentSquare.col)
                return true
            }
            currentSquare.dupe(destSquare)
            destSquare.clearSquare()

            if (currentSquare.row == 6) {
                val doubleMoveRow = currentSquare.row - 2
                if (!chessboard.getSquare(doubleMoveRow, currentSquare.col).isOccupied) {
                    destSquare = chessboard.getSquare(doubleMoveRow, currentSquare.col)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(doubleMoveRow, currentSquare.col)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            }
        }

        val leftDiagonalRow = currentSquare.row - 1
        val leftDiagonalCol = currentSquare.col - 1
        val rightDiagonalRow = currentSquare.row - 1
        val rightDiagonalCol = currentSquare.col + 1

        if (chessboard.isValidSquare(leftDiagonalRow, leftDiagonalCol) &&
            chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).isOccupied &&
            chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).pieceColor != currentSquare.pieceColor) {
            val destSquare = chessboard.getSquare(leftDiagonalRow, leftDiagonalCol)
            val tmpIsOccupied = destSquare.isOccupied
            val tmpPieceType = destSquare.pieceType
            val tmpPieceColor = destSquare.pieceColor
            val tmpImageView = destSquare.imageView
            destSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView
                addHighlightOpponent(leftDiagonalRow, leftDiagonalCol)
                return true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView
        }

        if (chessboard.isValidSquare(rightDiagonalRow, rightDiagonalCol) &&
            chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).isOccupied &&
            chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).pieceColor != currentSquare.pieceColor) {
            val destSquare = chessboard.getSquare(rightDiagonalRow, rightDiagonalCol)
            val tmpIsOccupied = destSquare.isOccupied
            val tmpPieceType = destSquare.pieceType
            val tmpPieceColor = destSquare.pieceColor
            val tmpImageView = destSquare.imageView
            destSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView
                addHighlightOpponent(rightDiagonalRow, rightDiagonalCol)
                return true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView
        }

        return false
    }

    fun canTakePinPiece(): Boolean{
        val leftDiagonalRow = currentSquare.row - 1
        val leftDiagonalCol = currentSquare.col - 1
        val rightDiagonalRow = currentSquare.row - 1
        val rightDiagonalCol = currentSquare.col + 1

        if (chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).isOccupied &&
            chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).pieceColor != currentSquare.pieceColor){
            addHighlightOpponent(leftDiagonalRow, leftDiagonalCol)
            return true
        }
        if (chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).isOccupied &&
            chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).pieceColor != currentSquare.pieceColor){
            addHighlightOpponent(rightDiagonalRow, rightDiagonalCol)
            return true
        }

        return false
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