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

        if (destinationSquare.pieceType == PieceType.KING) return false

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

                val sourceIsOccupied = currentSquare.isOccupied
                val sourcePieceType = currentSquare.pieceType
                val sourcePieceColor = currentSquare.pieceColor
                val sourceImageView = currentSquare.imageView
                currentSquare.clearSquare()

                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                    destinationSquare.pieceColor = tmpPieceColor
                    destinationSquare.pieceType = tmpPieceType
                    destinationSquare.isOccupied = tmpIsOccupied
                    destinationSquare.imageView = tmpImageView

                    currentSquare.pieceColor = sourcePieceColor
                    currentSquare.pieceType = sourcePieceType
                    currentSquare.isOccupied = sourceIsOccupied
                    currentSquare.imageView = sourceImageView
                    return true
                }
                destinationSquare.pieceColor = tmpPieceColor
                destinationSquare.pieceType = tmpPieceType
                destinationSquare.isOccupied = tmpIsOccupied
                destinationSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
            }

            if (chessboard.getSquare(destinationSquare.row, destinationSquare.col).pieceColor != currentSquare.pieceColor &&
                destinationSquare.row == rightDiagonalRow && destinationSquare.col == rightDiagonalCol) {
                val tmpIsOccupied = destinationSquare.isOccupied
                val tmpPieceType = destinationSquare.pieceType
                val tmpPieceColor = destinationSquare.pieceColor
                val tmpImageView = destinationSquare.imageView
                destinationSquare.clearSquare()

                val sourceIsOccupied = currentSquare.isOccupied
                val sourcePieceType = currentSquare.pieceType
                val sourcePieceColor = currentSquare.pieceColor
                val sourceImageView = currentSquare.imageView
                currentSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                    destinationSquare.pieceColor = tmpPieceColor
                    destinationSquare.pieceType = tmpPieceType
                    destinationSquare.isOccupied = tmpIsOccupied
                    destinationSquare.imageView = tmpImageView

                    currentSquare.pieceColor = sourcePieceColor
                    currentSquare.pieceType = sourcePieceType
                    currentSquare.isOccupied = sourceIsOccupied
                    currentSquare.imageView = sourceImageView
                    return true
                }
                destinationSquare.pieceColor = tmpPieceColor
                destinationSquare.pieceType = tmpPieceType
                destinationSquare.isOccupied = tmpIsOccupied
                destinationSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
            }
        }
        return false
    }

    fun isValidEnPassantMove(destinationSquare: Square, lastOpponentMove: MoveTracker?): Boolean {
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        if (lastOpponentMove != null){
            val opponentPawnSquare = chessboard.getSquare(lastOpponentMove.destinationSquare.row, lastOpponentMove.destinationSquare.col)
            val tmpIsOccupied = opponentPawnSquare.isOccupied
            val tmpPieceType = opponentPawnSquare.pieceType
            val tmpPieceColor = opponentPawnSquare.pieceColor
            val tmpImageView = opponentPawnSquare.imageView
            opponentPawnSquare.clearSquare()
            destinationSquare.dupe(currentSquare)
            currentSquare.clearSquare()
            if (destinationSquare.row == lastOpponentMove.sourceSquare.row + 1 && destinationSquare.col == lastOpponentMove.sourceSquare.col &&
                !chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                opponentPawnSquare.pieceColor = tmpPieceColor
                opponentPawnSquare.pieceType = tmpPieceType
                opponentPawnSquare.isOccupied = tmpIsOccupied
                opponentPawnSquare.imageView = tmpImageView
                currentSquare.dupe(destinationSquare)
                destinationSquare.clearSquare()
                return true
            }
            opponentPawnSquare.pieceColor = tmpPieceColor
            opponentPawnSquare.pieceType = tmpPieceType
            opponentPawnSquare.isOccupied = tmpIsOccupied
            opponentPawnSquare.imageView = tmpImageView
            currentSquare.dupe(destinationSquare)
            destinationSquare.clearSquare()
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

    fun showEnPassantSquare(lastOpponentMove: MoveTracker){
        addHighlightSquare(lastOpponentMove.sourceSquare.row + 1, lastOpponentMove.sourceSquare.col)
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

            val sourceIsOccupied = currentSquare.isOccupied
            val sourcePieceType = currentSquare.pieceType
            val sourcePieceColor = currentSquare.pieceColor
            val sourceImageView = currentSquare.imageView
            currentSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
                addHighlightOpponent(leftDiagonalRow, leftDiagonalCol)
                return true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView

            currentSquare.pieceColor = sourcePieceColor
            currentSquare.pieceType = sourcePieceType
            currentSquare.isOccupied = sourceIsOccupied
            currentSquare.imageView = sourceImageView
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

            val sourceIsOccupied = currentSquare.isOccupied
            val sourcePieceType = currentSquare.pieceType
            val sourcePieceColor = currentSquare.pieceColor
            val sourceImageView = currentSquare.imageView
            currentSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
                addHighlightOpponent(rightDiagonalRow, rightDiagonalCol)
                return true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView

            currentSquare.pieceColor = sourcePieceColor
            currentSquare.pieceType = sourcePieceType
            currentSquare.isOccupied = sourceIsOccupied
            currentSquare.imageView = sourceImageView
        }

        return false
    }

    fun canTakePinPiece(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        var result = false
        val leftDiagonalRow = currentSquare.row - 1
        val leftDiagonalCol = currentSquare.col - 1
        val rightDiagonalRow = currentSquare.row - 1
        val rightDiagonalCol = currentSquare.col + 1

        if (chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).isOccupied &&
            chessboard.getSquare(leftDiagonalRow, leftDiagonalCol).pieceColor != currentSquare.pieceColor){
            val destSquare = chessboard.getSquare(leftDiagonalRow, leftDiagonalCol)
            val tmpIsOccupied = destSquare.isOccupied
            val tmpPieceType = destSquare.pieceType
            val tmpPieceColor = destSquare.pieceColor
            val tmpImageView = destSquare.imageView
            destSquare.clearSquare()

            val sourceIsOccupied = currentSquare.isOccupied
            val sourcePieceType = currentSquare.pieceType
            val sourcePieceColor = currentSquare.pieceColor
            val sourceImageView = currentSquare.imageView
            currentSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
                addHighlightOpponent(leftDiagonalRow, leftDiagonalCol)
                result = true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView

            currentSquare.pieceColor = sourcePieceColor
            currentSquare.pieceType = sourcePieceType
            currentSquare.isOccupied = sourceIsOccupied
            currentSquare.imageView = sourceImageView
        }

        if (chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).isOccupied &&
            chessboard.getSquare(rightDiagonalRow, rightDiagonalCol).pieceColor != currentSquare.pieceColor){
            val destSquare = chessboard.getSquare(rightDiagonalRow, rightDiagonalCol)
            val tmpIsOccupied = destSquare.isOccupied
            val tmpPieceType = destSquare.pieceType
            val tmpPieceColor = destSquare.pieceColor
            val tmpImageView = destSquare.imageView
            destSquare.clearSquare()

            val sourceIsOccupied = currentSquare.isOccupied
            val sourcePieceType = currentSquare.pieceType
            val sourcePieceColor = currentSquare.pieceColor
            val sourceImageView = currentSquare.imageView
            currentSquare.clearSquare()
            if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                destSquare.pieceColor = tmpPieceColor
                destSquare.pieceType = tmpPieceType
                destSquare.isOccupied = tmpIsOccupied
                destSquare.imageView = tmpImageView

                currentSquare.pieceColor = sourcePieceColor
                currentSquare.pieceType = sourcePieceType
                currentSquare.isOccupied = sourceIsOccupied
                currentSquare.imageView = sourceImageView
                addHighlightOpponent(rightDiagonalRow, rightDiagonalCol)
                result = true
            }
            destSquare.pieceColor = tmpPieceColor
            destSquare.pieceType = tmpPieceType
            destSquare.isOccupied = tmpIsOccupied
            destSquare.imageView = tmpImageView

            currentSquare.pieceColor = sourcePieceColor
            currentSquare.pieceType = sourcePieceType
            currentSquare.isOccupied = sourceIsOccupied
            currentSquare.imageView = sourceImageView
        }

        return result
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
        if (square.pieceType == PieceType.KING) return
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