package com.example.chessmate.util.chess.bitboard

import com.example.chessmate.util.chess.GamePhase
import com.example.chessmate.util.chess.chessboard.PieceColor

object GameContext {
    var gamePhase: GamePhase = GamePhase.NONE
    var playerColor: PieceColor = PieceColor.NONE
    var botColor: PieceColor = PieceColor.NONE
}