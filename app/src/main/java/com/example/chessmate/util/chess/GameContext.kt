package com.example.chessmate.util.chess

object GameContext {
    var gamePhase: GamePhase = GamePhase.NONE
    var playerColor: PieceColor = PieceColor.NONE
    var botColor: PieceColor = PieceColor.NONE
    var isPlayerTurn: Boolean = false
    var isBotTurn: Boolean = false
    var topMoveSearch: Int = 3
    var depth: Int = 4
}