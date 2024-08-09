package com.example.chessmate.util.chess

object FENHolder {
    private var fen: FEN? = null

    fun setFEN(fen: FEN?) {
        this.fen = fen
    }

    fun getFEN(): FEN? {
        return this.fen
    }
}