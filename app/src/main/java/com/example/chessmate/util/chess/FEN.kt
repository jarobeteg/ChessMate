package com.example.chessmate.util.chess

import com.example.chessmate.R
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator

class FEN(private val fenString: String) {

    var piecePlacement: String = ""
    var activeColor: Char = 'w'
    var castlingRights: String = "-"
    var enPassantTarget: String = "-"
    var halfMoveClock: Int = 0
    var fullMove: Int = 1
    var isValid: Boolean = false
        private set
    var validationMessage: Int = 0

    init {
        parseFEN()
    }

    private fun parseFEN() {
        if (isFormatValid()) {
            val parts = fenString.split(" ")
            piecePlacement = parts[0]
            activeColor = parts[1][0]
            castlingRights = parts[2]
            enPassantTarget = parts[3]
            halfMoveClock = parts[4].toInt()
            fullMove = parts[5].toInt()

            if (activeColor == 'w') {
                if (GameContext.playerColor == PieceColor.WHITE) {
                    GameContext.isPlayerTurn = true
                    GameContext.isBotTurn = false
                } else {
                    GameContext.isPlayerTurn = false
                    GameContext.isBotTurn = true
                }
            } else {
                if (GameContext.playerColor == PieceColor.BLACK) {
                    GameContext.isPlayerTurn = true
                    GameContext.isBotTurn = false
                } else {
                    GameContext.isPlayerTurn = false
                    GameContext.isBotTurn = true
                }
            }

            if (isPositionLegal()) {
                isValid = true
            } else {
                isValid = false
                validationMessage = R.string.valid_FEN_string_illegal_position
            }
        } else {
            isValid = false
            validationMessage = R.string.invalid_FEN_string
        }
    }

    private fun isFormatValid(): Boolean {
        val fenPattern = Regex("^(?:[pnbrqkPNBRQK1-8]{1,8}/){7}[pnbrqkPNBRQK1-8]{1,8} [wb] (?:[KQkq]{1,4}|-) (?:[a-h][36]|-) \\d+ \\d+$")
        return fenString.matches(fenPattern)
    }

    private fun isPositionLegal(): Boolean {
        val bitboard = Bitboard()
        bitboard.setupFENPosition(this)

        val whiteKingCount = java.lang.Long.bitCount(bitboard.getWhiteKing())
        val blackKingCount = java.lang.Long.bitCount(bitboard.getBlackKing())

        if (whiteKingCount != 1 || blackKingCount != 1) return false

        if (bitboard.isGameEnded()) return false

        return true
    }

    override fun toString(): String {
        return fenString
    }
}