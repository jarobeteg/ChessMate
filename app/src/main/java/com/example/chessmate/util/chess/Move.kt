package com.example.chessmate.util.chess

data class Move(val startSquare: Square, val destSquare: Square, val score: Int = 0, val isCapture: Boolean = false)
