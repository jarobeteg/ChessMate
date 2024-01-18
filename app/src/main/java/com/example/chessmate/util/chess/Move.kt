package com.example.chessmate.util.chess

data class Move(val startSquare: Square, val destSquare: Square, val isCapture: Boolean = false)
