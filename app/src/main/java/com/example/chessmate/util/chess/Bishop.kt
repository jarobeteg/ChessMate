package com.example.chessmate.util.chess

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.chessmate.R
import kotlin.math.abs

class Bishop(private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard, private var currentSquare: Square){
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    fun isValidMove(destinationSquare: Square): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        val rowDiff = abs(destinationSquare.row - currentSquare.row)
        val colDiff = abs(destinationSquare.col - currentSquare.col)

        if (destinationSquare.pieceType == PieceType.KING) return false

        if (rowDiff != colDiff) {
            return false
        }

        val rowIncrement = if (destinationSquare.row > currentSquare.row) 1 else -1
        val colIncrement = if (destinationSquare.col > currentSquare.col) 1 else -1

        var r = currentSquare.row + rowIncrement
        var c = currentSquare.col + colIncrement
        while (r != destinationSquare.row && c != destinationSquare.col) {
            if (chessboard.getSquare(r, c).isOccupied) {
                return false
            }
            r += rowIncrement
            c += colIncrement
        }
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

        destinationSquare.pieceColor = sourcePieceColor
        destinationSquare.pieceType = sourcePieceType
        destinationSquare.isOccupied = sourceIsOccupied
        destinationSquare.imageView = sourceImageView

        if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)) {
            destinationSquare.clearSquare()
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
        return false
    }

    fun showHighlightSquares() {
        val row = currentSquare.row
        val col = currentSquare.col

        var r = row - 1
        var c = col + 1
        while (r >= 0 && c < 8) {
            if (chessboard.isValidSquare(r, c)) {
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
            if (chessboard.isValidSquare(r, c)) {
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
            if (chessboard.isValidSquare(r, c)) {
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
            if (chessboard.isValidSquare(r, c)) {
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

    fun canCheckBeBlocked(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        val row = currentSquare.row
        val col = currentSquare.col

        var r = row - 1
        var c = col + 1
        while (r >= 0 && c < 8) {
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
                    destSquare.dupe(currentSquare)
                    currentSquare.clearSquare()
                    if (!chessboard.isKingInCheck(chessboard, kingPosition!!, kingPosition.pieceColor!!)){
                        currentSquare.dupe(destSquare)
                        destSquare.clearSquare()
                        addHighlightSquare(r, c)
                        return true
                    }
                    currentSquare.dupe(destSquare)
                    destSquare.clearSquare()
                }
            } else {
                break
            }
            r++
            c++
        }

        return false
    }

    fun canTakePinPiece(): Boolean{
        val kingPosition = if (currentSquare.pieceColor == PieceColor.WHITE) chessboard.getWhiteKingSquare() else chessboard.getBlackKingSquare()
        var result = false
        val row = currentSquare.row
        val col = currentSquare.col

        var r = row - 1
        var c = col + 1
        while (r >= 0 && c < 8) {
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
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
                        addHighlightSquare(r, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
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
                        addHighlightSquare(r, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
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
                        addHighlightSquare(r, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
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
            if (chessboard.isValidSquare(r, c)) {
                if (chessboard.getSquare(r, c).isOccupied) {
                    if (chessboard.getSquare(r, c).pieceColor != currentSquare.pieceColor) {
                        val destSquare = chessboard.getSquare(r, c)
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
                            addHighlightOpponent(r, c)
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
                }else{
                    val destSquare = chessboard.getSquare(r, c)
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
                        addHighlightSquare(r, c)
                        result = true
                    }
                    currentSquare.pieceColor = tmpPieceColor
                    currentSquare.pieceType = tmpPieceType
                    currentSquare.isOccupied = tmpIsOccupied
                    currentSquare.imageView = tmpImageView
                    destSquare.clearSquare()
                }
            } else {
                break
            }
            r++
            c++
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