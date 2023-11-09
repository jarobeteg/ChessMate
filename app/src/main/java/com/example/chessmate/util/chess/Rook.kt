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
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()

        if (destinationSquare.pieceType == PieceType.KING) return false

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
                destinationSquare.dupe(currentSquare)
                currentSquare.clearSquare()
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                    currentSquare.dupe(destinationSquare)
                    destinationSquare.clearSquare()
                    return true
                }
                currentSquare.dupe(destinationSquare)
                destinationSquare.clearSquare()
            } else {
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
                if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
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

    fun showHighlightSquares(){
        val row = currentSquare.row
        val col = currentSquare.col
        for (r in row - 1 downTo 0){
            if (chessboard.isValidSquare(r, currentSquare.col)) {
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
            if (chessboard.isValidSquare(r, currentSquare.col)) {
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
            if (chessboard.isValidSquare(currentSquare.row, c)) {
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
            if (chessboard.isValidSquare(currentSquare.row, c)) {
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

    fun canCheckBeBlocked(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()

        val row = currentSquare.row
        val col = currentSquare.col
        for (r in row - 1 downTo 0){
            if (chessboard.isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, currentSquare.col)
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
                            addHighlightOpponent(r, currentSquare.col)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(r, currentSquare.col)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, currentSquare.col)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            }
        }

        for (r in row + 1 until 8){
            if (chessboard.isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, currentSquare.col)
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
                            addHighlightOpponent(r, currentSquare.col)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(r, currentSquare.col)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, currentSquare.col)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            }
        }

        for (c in col - 1 downTo  0){
            if (chessboard.isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(currentSquare.row, c)
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
                            addHighlightOpponent(currentSquare.row, c)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(currentSquare.row, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(currentSquare.row, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            }
        }

        for (c in col + 1 until 8){
            if (chessboard.isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(currentSquare.row, c)
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
                            addHighlightOpponent(currentSquare.row, c)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(currentSquare.row, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(currentSquare.row, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            }
        }
        return false
    }

    fun canTakePinPiece(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        var result = false
        val row = currentSquare.row
        val col = currentSquare.col
        for (r in row - 1 downTo 0){
            if (chessboard.isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, currentSquare.col)
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
                            addHighlightOpponent(r, currentSquare.col)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(r, currentSquare.col)
                    destSquare.dupe(currentSquare)
                    val tmpIsOccupied = currentSquare.isOccupied
                    val tmpPieceType = currentSquare.pieceType
                    val tmpPieceColor = currentSquare.pieceColor
                    val tmpImageView = currentSquare.imageView
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.pieceColor = tmpPieceColor
                        currentSquare.pieceType = tmpPieceType
                        currentSquare.isOccupied = tmpIsOccupied
                        currentSquare.imageView = tmpImageView
                        destSquare.clearSquare()
                        addHighlightSquare(r, currentSquare.col)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
                }
            }
        }

        for (r in row + 1 until 8){
            if (chessboard.isValidSquare(r, currentSquare.col)) {
                if (chessboard.getSquare(r, currentSquare.col).isOccupied) {
                    if (chessboard.getSquare(r, currentSquare.col).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, currentSquare.col)
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
                            addHighlightOpponent(r, currentSquare.col)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(r, currentSquare.col)
                    destSquare.dupe(currentSquare)
                    val tmpIsOccupied = currentSquare.isOccupied
                    val tmpPieceType = currentSquare.pieceType
                    val tmpPieceColor = currentSquare.pieceColor
                    val tmpImageView = currentSquare.imageView
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.pieceColor = tmpPieceColor
                        currentSquare.pieceType = tmpPieceType
                        currentSquare.isOccupied = tmpIsOccupied
                        currentSquare.imageView = tmpImageView
                        destSquare.clearSquare()
                        addHighlightSquare(r, currentSquare.col)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
                }
            }
        }

        for (c in col - 1 downTo  0){
            if (chessboard.isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(currentSquare.row, c)
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
                            addHighlightOpponent(currentSquare.row, c)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(currentSquare.row, c)
                    destSquare.dupe(currentSquare)
                    val tmpIsOccupied = currentSquare.isOccupied
                    val tmpPieceType = currentSquare.pieceType
                    val tmpPieceColor = currentSquare.pieceColor
                    val tmpImageView = currentSquare.imageView
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.pieceColor = tmpPieceColor
                        currentSquare.pieceType = tmpPieceType
                        currentSquare.isOccupied = tmpIsOccupied
                        currentSquare.imageView = tmpImageView
                        destSquare.clearSquare()
                        addHighlightSquare(currentSquare.row, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
                }
            }
        }

        for (c in col + 1 until 8){
            if (chessboard.isValidSquare(currentSquare.row, c)) {
                if (chessboard.getSquare(currentSquare.row, c).isOccupied) {
                    if (chessboard.getSquare(currentSquare.row, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(currentSquare.row, c)
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
                            addHighlightOpponent(currentSquare.row, c)
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
                    break
                } else {
                    val destSquare = chessboard.getSquare(currentSquare.row, c)
                    destSquare.dupe(currentSquare)
                    val tmpIsOccupied = currentSquare.isOccupied
                    val tmpPieceType = currentSquare.pieceType
                    val tmpPieceColor = currentSquare.pieceColor
                    val tmpImageView = currentSquare.imageView
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.pieceColor = tmpPieceColor
                        currentSquare.pieceType = tmpPieceType
                        currentSquare.isOccupied = tmpIsOccupied
                        currentSquare.imageView = tmpImageView
                        destSquare.clearSquare()
                        addHighlightSquare(currentSquare.row, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
                }
            }
        }
        return result
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