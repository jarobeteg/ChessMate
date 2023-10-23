package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chessmate.R

class GameChess : AbsThemeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_game)

        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        var startingSide = sharedPreferences.getString("starting_side", "random")
        if (startingSide == "random") {
            val random = java.util.Random()
            val isWhite = random.nextBoolean()
            startingSide = if (isWhite) "white" else "black"
        }

        val chessboard = findViewById<GridLayout>(R.id.chessboard)
        val screenWidth = resources.displayMetrics.widthPixels
        val squareSize = screenWidth / 8

        if (startingSide == "white") {
            setupChessboard(chessboard, squareSize, true)
        } else {
            setupChessboard(chessboard, squareSize, false)
        }
    }

    private fun setupChessboard(chessboard: GridLayout, squareSize: Int, isWhiteStarting: Boolean) {
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color

        val boardState: Array<Array<Int>> = Array(8) { Array(8) { 0 } }

        //here i add numbers to the boardState array so later i can determine the piece type
        for (col in 0 until 8) {
            if (isWhiteStarting) {
                boardState[6][col] = 1 //white pawns
                boardState[7][0] = 2 //white rook
                boardState[7][1] = 3 //white knight
                boardState[7][2] = 4 //white bishop
                boardState[7][3] = 5 //white queen
                boardState[7][4] = 6 //white king
                boardState[7][5] = 4 //white bishop
                boardState[7][6] = 3 //white knight
                boardState[7][7] = 2 //white rook
                boardState[1][col] = 7 //black pawns
                boardState[0][0] = 8 //black rook
                boardState[0][1] = 9 //black knight
                boardState[0][2] = 10 //black bishop
                boardState[0][3] = 11 //black queen
                boardState[0][4] = 12 //black king
                boardState[0][5] = 10 //black bishop
                boardState[0][6] = 9 //black knight
                boardState[0][7] = 8 //black rook
            }else{
                boardState[1][col] = 1 //white pawns
                boardState[0][0] = 2 //white rook
                boardState[0][1] = 3 //white knight
                boardState[0][2] = 4 //white bishop
                boardState[0][3] = 6 //white king
                boardState[0][4] = 5 //white queen
                boardState[0][5] = 4 //white bishop
                boardState[0][6] = 3 //white knight
                boardState[0][7] = 2 //white rook
                boardState[6][col] = 7
                boardState[7][0] = 8 //black rook
                boardState[7][1] = 9 //black knight
                boardState[7][2] = 10 //black bishop
                boardState[7][3] = 12 //black king
                boardState[7][4] = 11 //black queen
                boardState[7][5] = 10 //black bishop
                boardState[7][6] = 9 //black knight
                boardState[7][7] = 8 //black rook
            }
        }

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val frameLayout = FrameLayout(this)
                frameLayout.layoutParams = GridLayout.LayoutParams().apply {
                    width = squareSize
                    height = squareSize
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                }

                val isLight = (row + col) % 2 == 0
                val colorResId = if (isLight) lightSquareColor else darkSquareColor

                val square = ImageView(this)
                square.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                square.setBackgroundColor(ContextCompat.getColor(this, colorResId))

                //piece type 1 is white pawn, 2 is white rook, 3 is white knight, 4 is white bishop, 5 is white queen, 6 is white king
                //piece type 7 is black pawn, 8 is black rook, 9 is black knight, 10 is black bishop, 11 is black queen, 12 is black king
                val pieceType = boardState[row][col]
                when (pieceType){
                    1 -> square.setImageResource(R.drawable.default_pawn_white)
                    2 -> square.setImageResource(R.drawable.default_rook_white)
                    3 -> square.setImageResource(R.drawable.default_knight_white)
                    4 -> square.setImageResource(R.drawable.default_bishop_white)
                    5 -> square.setImageResource(R.drawable.default_queen_white)
                    6 -> square.setImageResource(R.drawable.default_king_white)
                    7 -> square.setImageResource(R.drawable.default_pawn_black)
                    8 -> square.setImageResource(R.drawable.default_rook_black)
                    9 -> square.setImageResource(R.drawable.default_knight_black)
                    10 -> square.setImageResource(R.drawable.default_bishop_black)
                    11 -> square.setImageResource(R.drawable.default_queen_black)
                    12 -> square.setImageResource(R.drawable.default_king_black)
                }

                frameLayout.addView(square)

                if (col == 0) {
                    val numberTextView = TextView(this)
                    val number = if (isWhiteStarting) (8 - row).toString() else (row + 1).toString()
                    numberTextView.text = number
                    numberTextView.setTextColor(getNumberTextColor(row))
                    numberTextView.setPadding(4, 0, 0, 0)
                    numberTextView.gravity = Gravity.START or Gravity.TOP
                    numberTextView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    frameLayout.addView(numberTextView)
                }

                if (row == 7) {
                    val letterTextView = TextView(this)
                    val letter = if (isWhiteStarting) ('a' + col).toString() else ('h' - col).toString()
                    letterTextView.text = letter
                    letterTextView.setTextColor(getLetterTextColor(col))
                    letterTextView.setPadding(0, 0, 4, 0)
                    letterTextView.gravity = Gravity.END or Gravity.BOTTOM
                    letterTextView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    frameLayout.addView(letterTextView)
                }

                chessboard.addView(frameLayout)
            }
        }
    }

    private fun getNumberTextColor(row: Int): Int {
        return if (row % 2 == 0) getColor(R.color.default_dark_square_color)
        else getColor(R.color.default_light_square_color)
    }

    private fun getLetterTextColor(col: Int): Int {
        return if (col % 2 == 0) getColor(R.color.default_light_square_color)
        else getColor(R.color.default_dark_square_color)
    }
}