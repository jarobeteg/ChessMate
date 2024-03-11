package com.example.chessmate.util.chess

data class Move(val fromRow: Int, val fromCol: Int, val toRow: Int, val toCol: Int, val score: Int = 0, val isCapture: Boolean = false)
