package com.example.chessmate.util.chess

import android.content.Context
import android.widget.GridLayout

class ChessBot(private val botColor: PieceColor, private var context: Context, private var chessboardLayout: GridLayout, private var chessboard: Chessboard){
    init {
        println("The chess bot color is: $botColor")
    }

    fun makeBestMove(){
        //before generating moves you need to check if the chessbot king is in check
        println("making best move")
        val legalMoves = generateLegalMoves()
        for (l in legalMoves){
            //println("Start square row: ${l.startSquare.row}, start square col: ${l.startSquare.col}")
            println("Dest square row: ${l.destSquare.row}, dest square col: ${l.destSquare.col}")
        }
    }

    private fun generateLegalMoves(): List<Move>{
        val legalMoves = mutableListOf<Move>()

        for (row in 0 until 8){
            for (col in 0 until 8){
                val piece = chessboard.getSquare(row, col)
                if (piece.pieceColor == botColor){
                    when (piece.pieceType){
                        PieceType.PAWN -> legalMoves.addAll(generatePawnMoves(row, col))
                        PieceType.KNIGHT -> legalMoves.addAll(generateKnightMoves(row, col))
                        PieceType.BISHOP -> legalMoves.addAll(generateBishopMoves(row, col))
                        PieceType.ROOK -> legalMoves.addAll(generateRookMoves(row, col))
                        PieceType.QUEEN -> legalMoves.addAll(generateQueenMoves(row, col))
                        else -> {}
                    }
                }
            }
        }

        return legalMoves
    }

    private fun generatePawnMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()
        val startSquare: Square = chessboard.getSquare(row, col)
        var destSquare: Square
        val direction = 1

        if (chessboard.isEmptySquare(row + direction, col)){
            destSquare = chessboard.getSquare(row + direction, col)
            legalMoves.add(Move(startSquare, destSquare))
        }

        if (row == 1){
            if (chessboard.isEmptySquare(row + direction, col) &&
                chessboard.isEmptySquare(row + 2 * direction, col)){
                destSquare = chessboard.getSquare(row + 2 * direction, col)
                legalMoves.add(Move(startSquare, destSquare))
            }
        }

        if (chessboard.isValidSquare(row + direction, col - direction) &&
            chessboard.isOpponentPiece(row + direction, col - direction, startSquare)){
            destSquare = chessboard.getSquare(row + direction, col - direction)
            legalMoves.add(Move(startSquare, destSquare))
        }
        if (chessboard.isValidSquare(row + direction, col + direction) &&
            chessboard.isOpponentPiece(row + direction, col + direction, startSquare)){
            destSquare = chessboard.getSquare(row + direction, col + direction)
            legalMoves.add(Move(startSquare, destSquare))
        }

        return legalMoves
    }

    private fun generateKnightMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()
        val startSquare = chessboard.getSquare(row, col)
        var destSquare: Square
        val moves = arrayOf(
            Pair(-2, -1), Pair(-2, 1),
            Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2),
            Pair(2, -1), Pair(2, 1)
        )

        for ((rowOffset, colOffset) in moves){
            val newRow = row + rowOffset
            val newCol = col + colOffset

            if (chessboard.isValidSquare(newRow, newCol) &&
                (chessboard.isEmptySquare(newRow,newCol) || chessboard.isOpponentPiece(newRow, newCol, startSquare))){
                destSquare = chessboard.getSquare(newRow, newCol)
                legalMoves.add(Move(startSquare, destSquare))
            }
        }

        return legalMoves
    }

    private fun generateBishopMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()
        val startSquare = chessboard.getSquare(row, col)
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 1),
            Pair(1, -1), Pair(1, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (chessboard.isValidSquare(newRow, newCol)){
                destSquare = chessboard.getSquare(newRow, newCol)
                if (chessboard.isEmptySquare(newRow, newCol)){
                    legalMoves.add(Move(startSquare, destSquare))
                } else if (chessboard.isOpponentPiece(newRow, newCol, startSquare)){
                    legalMoves.add(Move(startSquare, destSquare))
                    break
                } else {
                    break
                }

                newRow += rowOffset
                newCol += colOffset
            }
        }

        return legalMoves
    }

    private fun generateRookMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()
        val startSquare = chessboard.getSquare(row, col)
        var destSquare: Square
        val directions = arrayOf(
            Pair(-1, 0), Pair(1, 0),
            Pair(0, -1), Pair(0, 1)
        )

        for ((rowOffset, colOffset) in directions){
            var newRow = row + rowOffset
            var newCol = col + colOffset

            while (chessboard.isValidSquare(newRow, newCol)){
                destSquare = chessboard.getSquare(newRow, newCol)
                if (chessboard.isEmptySquare(newRow, newCol)){
                    legalMoves.add(Move(startSquare, destSquare))
                } else if (chessboard.isOpponentPiece(newRow, newCol, startSquare)){
                    legalMoves.add(Move(startSquare, destSquare))
                    break
                } else {
                    break
                }

                newRow += rowOffset
                newCol += colOffset
            }
        }

        return legalMoves
    }

    private fun generateQueenMoves(row: Int, col: Int): List<Move>{
        val legalMoves = mutableListOf<Move>()

        legalMoves.addAll(generateBishopMoves(row, col))
        legalMoves.addAll(generateRookMoves(row, col))

        return legalMoves
    }
}