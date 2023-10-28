package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.chessmate.R
import com.example.chessmate.util.chess.Bishop
import com.example.chessmate.util.chess.ChessColor
import com.example.chessmate.util.chess.ChessPiece
import com.example.chessmate.util.chess.Chessboard
import com.example.chessmate.util.chess.King
import com.example.chessmate.util.chess.Knight
import com.example.chessmate.util.chess.Pawn
import com.example.chessmate.util.chess.Queen
import com.example.chessmate.util.chess.Rook
import com.example.chessmate.util.chess.Square

class GameChess : AbsThemeActivity() {
    private lateinit var chessboard: Chessboard
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

        val chessboardLayout = findViewById<GridLayout>(R.id.chessboard)
        val screenWidth = resources.displayMetrics.widthPixels
        val squareSize = screenWidth / 8

        chessboard = Chessboard()

        if (startingSide == "white"){
            initializeStartingPosition(true)
            setupChessboard(chessboardLayout, squareSize, true)
        }else{
            initializeStartingPosition(false)
            setupChessboard(chessboardLayout, squareSize, false)
        }
    }

    private fun setupChessboard(chessboardLayout: GridLayout, squareSize: Int, isWhiteStarting: Boolean){
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = chessboard.getSquare(row, col)
                val piece = square?.piece
                val frameLayout = FrameLayout(this)
                frameLayout.layoutParams = GridLayout.LayoutParams().apply {
                    width = squareSize
                    height = squareSize
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                }

                val colorResId = if ((row + col) % 2 == 0) lightSquareColor else darkSquareColor
                frameLayout.setBackgroundResource(colorResId)

                if (piece != null) {
                    val pieceImageResId = getPieceImageResource(piece)
                    val imageView = ImageView(this)
                    imageView.setImageResource(pieceImageResId)
                    imageView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    frameLayout.addView(imageView)
                }

                if (col == 0) {
                    val numberTextView = TextView(this)
                    val number = if (isWhiteStarting) (8 - row).toString() else (row + 1).toString()
                    numberTextView.text = number
                    val textSizeInSp = 10
                    numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat())
                    numberTextView.setTextColor(getNumberTextColor(row))
                    numberTextView.setPadding(2, 0, 0, 0)
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
                    val textSizeInSp = 10
                    letterTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat())
                    letterTextView.setTextColor(getLetterTextColor(col))
                    letterTextView.setPadding(0, 0, 2, 0)
                    letterTextView.gravity = Gravity.END or Gravity.BOTTOM
                    letterTextView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    frameLayout.addView(letterTextView)
                }

                frameLayout.setOnClickListener {
                    handleSquareClick(square)
                }

                chessboardLayout.addView(frameLayout)
            }
        }
    }

    private fun initializeStartingPosition(isWhiteStarting: Boolean) {
        if (isWhiteStarting){
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,0), 6, 0)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,1), 6, 1)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,2), 6, 2)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,3), 6, 3)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,4), 6, 4)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,5), 6, 5)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,6), 6, 6)
            chessboard.placePiece(Pawn(ChessColor.WHITE,6,7), 6, 7)

            chessboard.placePiece(Rook(ChessColor.WHITE,7,0), 7, 0)
            chessboard.placePiece(Knight(ChessColor.WHITE,7,1), 7, 1)
            chessboard.placePiece(Bishop(ChessColor.WHITE,7,2), 7, 2)
            chessboard.placePiece(Queen(ChessColor.WHITE,7,3), 7, 3)
            chessboard.placePiece(King(ChessColor.WHITE,7,4), 7, 4)
            chessboard.placePiece(Bishop(ChessColor.WHITE,7,5), 7, 5)
            chessboard.placePiece(Knight(ChessColor.WHITE,7,6), 7, 6)
            chessboard.placePiece(Rook(ChessColor.WHITE,7,7), 7, 7)

            chessboard.placePiece(Pawn(ChessColor.BLACK,1,0), 1, 0)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,1), 1, 1)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,2), 1, 2)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,3), 1, 3)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,4), 1, 4)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,5), 1, 5)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,6), 1, 6)
            chessboard.placePiece(Pawn(ChessColor.BLACK,1,7), 1, 7)

            chessboard.placePiece(Rook(ChessColor.BLACK,0,0), 0, 0)
            chessboard.placePiece(Knight(ChessColor.BLACK,0,1), 0, 1)
            chessboard.placePiece(Bishop(ChessColor.BLACK,0,2), 0, 2)
            chessboard.placePiece(Queen(ChessColor.BLACK,0,3), 0, 3)
            chessboard.placePiece(King(ChessColor.BLACK,0,4), 0, 4)
            chessboard.placePiece(Bishop(ChessColor.BLACK,0,5), 0, 5)
            chessboard.placePiece(Knight(ChessColor.BLACK,0,6), 0, 6)
            chessboard.placePiece(Rook(ChessColor.BLACK,0,7), 0, 7)
        }else{
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,0), 1, 0)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,1), 1, 1)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,2), 1, 2)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,3), 1, 3)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,4), 1, 4)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,5), 1, 5)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,6), 1, 6)
            chessboard.placePiece(Pawn(ChessColor.WHITE,1,7), 1, 7)

            chessboard.placePiece(Rook(ChessColor.WHITE,0,0), 0, 0)
            chessboard.placePiece(Knight(ChessColor.WHITE,0,1), 0, 1)
            chessboard.placePiece(Bishop(ChessColor.WHITE,0,2), 0, 2)
            chessboard.placePiece(King(ChessColor.WHITE,0,3), 0, 3)
            chessboard.placePiece(Queen(ChessColor.WHITE,0,4), 0, 4)
            chessboard.placePiece(Bishop(ChessColor.WHITE,0,5), 0, 5)
            chessboard.placePiece(Knight(ChessColor.WHITE,0,6), 0, 6)
            chessboard.placePiece(Rook(ChessColor.WHITE,0,7), 0, 7)

            chessboard.placePiece(Pawn(ChessColor.BLACK,6,0), 6, 0)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,1), 6, 1)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,2), 6, 2)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,3), 6, 3)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,4), 6, 4)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,5), 6, 5)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,6), 6, 6)
            chessboard.placePiece(Pawn(ChessColor.BLACK,6,7), 6, 7)

            chessboard.placePiece(Rook(ChessColor.BLACK,7,0), 7, 0)
            chessboard.placePiece(Knight(ChessColor.BLACK,7,1), 7, 1)
            chessboard.placePiece(Bishop(ChessColor.BLACK,7,2), 7, 2)
            chessboard.placePiece(King(ChessColor.BLACK,7,3), 7, 3)
            chessboard.placePiece(Queen(ChessColor.BLACK,7,4), 7, 4)
            chessboard.placePiece(Bishop(ChessColor.BLACK,7,5), 7, 5)
            chessboard.placePiece(Knight(ChessColor.BLACK,7,6), 7, 6)
            chessboard.placePiece(Rook(ChessColor.BLACK,7,7), 7, 7)
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

    private fun getPieceImageResource(piece: ChessPiece): Int {
        return when (piece){
            is Pawn -> if (piece.color == ChessColor.WHITE) R.drawable.default_pawn_white else R.drawable.default_pawn_black
            is Rook -> if (piece.color == ChessColor.WHITE) R.drawable.default_rook_white else R.drawable.default_rook_black
            is Knight -> if (piece.color == ChessColor.WHITE) R.drawable.default_knight_white else R.drawable.default_knight_black
            is Bishop -> if (piece.color == ChessColor.WHITE) R.drawable.default_bishop_white else R.drawable.default_bishop_black
            is Queen -> if (piece.color == ChessColor.WHITE) R.drawable.default_queen_white else R.drawable.default_queen_black
            is King -> if (piece.color == ChessColor.WHITE) R.drawable.default_king_white else R.drawable.default_king_black
            else -> 0
        }
    }

    private fun handleSquareClick(square: Square?) {
    }
}