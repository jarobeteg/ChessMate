package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.chessmate.R
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.Chessboard
import com.example.chessmate.util.chess.Pawn
import com.example.chessmate.util.chess.PieceType
import com.example.chessmate.util.chess.Square

class GameChess : AbsThemeActivity() {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var chessboard: Chessboard
    private var isWhiteStarting: Boolean = false
    private var squareSize: Int = 0
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private var selectedSquare: Square? = null
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

        chessboard = Chessboard()
        chessboardLayout = findViewById(R.id.chessboard)
        val screenWidth = resources.displayMetrics.widthPixels
        squareSize = screenWidth / 8

        if (startingSide == "white"){
            isWhiteStarting = true
            initializeStartingPosition()
            setupChessboard()
        }else{
            isWhiteStarting = false
            initializeStartingPosition()
            setupChessboard()
        }
    }

    private fun setupChessboard(){
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = chessboard.getSquare(row, col)
                val frameLayout = FrameLayout(this)
                frameLayout.layoutParams = GridLayout.LayoutParams().apply {
                    width = squareSize
                    height = squareSize
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                }

                val colorResId = if ((row + col) % 2 == 0) lightSquareColor else darkSquareColor
                frameLayout.setBackgroundResource(colorResId)

                if (square.isOccupied) {
                    val pieceImageView = createPieceImageView(square)
                    square.imageView = pieceImageView
                    frameLayout.addView(pieceImageView)
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

                square.frameLayout = frameLayout
                chessboardLayout.addView(frameLayout)
            }
        }
    }

    private fun initializeStartingPosition() {
        if (isWhiteStarting){
            chessboard.placePiece(6, 0, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 1, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 2, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 3, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 4, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 5, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 6, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 7, PieceColor.WHITE, PieceType.PAWN)

            chessboard.placePiece(7, 0, PieceColor.WHITE, PieceType.ROOK)
            chessboard.placePiece(7, 1, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(7, 2, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(7, 3, PieceColor.WHITE, PieceType.QUEEN)
            chessboard.placePiece(7, 4, PieceColor.WHITE, PieceType.KING)
            chessboard.placePiece(7, 5, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(7, 6, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(7, 7, PieceColor.WHITE, PieceType.ROOK)

            chessboard.placePiece(1, 0, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 1, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 2, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 3, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 4, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 5, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 6, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 7, PieceColor.BLACK, PieceType.PAWN)

            chessboard.placePiece(0, 0, PieceColor.BLACK, PieceType.ROOK)
            chessboard.placePiece(0, 1, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(0, 2, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(0, 3, PieceColor.BLACK, PieceType.QUEEN)
            chessboard.placePiece(0, 4, PieceColor.BLACK, PieceType.KING)
            chessboard.placePiece(0, 5, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(0, 6, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(0, 7, PieceColor.BLACK, PieceType.ROOK)
        }else{
            chessboard.placePiece(1, 0, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 1, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 2, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 3, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 4, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 5, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 6, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 7, PieceColor.WHITE, PieceType.PAWN)

            chessboard.placePiece(0, 0, PieceColor.WHITE, PieceType.ROOK)
            chessboard.placePiece(0, 1, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(0, 2, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(0, 3, PieceColor.WHITE, PieceType.KING)
            chessboard.placePiece(0, 4, PieceColor.WHITE, PieceType.QUEEN)
            chessboard.placePiece(0, 5, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(0, 6, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(0, 7, PieceColor.WHITE, PieceType.ROOK)

            chessboard.placePiece(6, 0, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 1, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 2, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 3, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 4, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 5, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 6, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 7, PieceColor.BLACK, PieceType.PAWN)

            chessboard.placePiece(7, 0, PieceColor.BLACK, PieceType.ROOK)
            chessboard.placePiece(7, 1, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(7, 2, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(7, 3, PieceColor.BLACK, PieceType.KING)
            chessboard.placePiece(7, 4, PieceColor.BLACK, PieceType.QUEEN)
            chessboard.placePiece(7, 5, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(7, 6, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(7, 7, PieceColor.BLACK, PieceType.ROOK)
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

    private fun createPieceImageView(square: Square): ImageView {
        val pieceImageView = ImageView(this)
        if (square.pieceColor == PieceColor.WHITE){
            when(square.pieceType){
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.default_pawn_white)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.default_rook_white)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.default_knight_white)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.default_bishop_white)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.default_queen_white)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.default_king_white)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }else{
            when(square.pieceType){
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.default_pawn_black)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.default_rook_black)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.default_knight_black)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.default_bishop_black)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.default_queen_black)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.default_king_black)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }
        return pieceImageView
    }

    private fun handleSquareClick(square: Square) {
        //the println is there for debugging this fucking mess. will get removed eventually
        println("row: ${square.row}, col: ${square.col}, PieceType: ${square.pieceType}, PieceColor: ${square.pieceColor}, FrameLayout: ${square.frameLayout}, ImageView: ${square.imageView}")

        when{
            isWhiteStarting && square.pieceColor == PieceColor.WHITE && selectedSquare == null -> { //first click as white
                removeHighlightCircles()
                removeHighlightOpponents()
                when(square.pieceType){
                    PieceType.PAWN -> {
                        val pawn = Pawn(this, chessboardLayout, chessboard, square)
                        pawn.showHighlightSquares()
                        selectedSquare = square
                    }
                    PieceType.ROOK -> {println("rook")}
                    PieceType.KNIGHT -> {println("knight")}
                    PieceType.BISHOP -> {println("bishop")}
                    PieceType.QUEEN -> {println("queen")}
                    PieceType.KING -> {println("king")}
                    else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                }
            }

            isWhiteStarting && selectedSquare != null -> { //second click as white
                if (selectedSquare == square){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                } else if (selectedSquare?.pieceColor == square.pieceColor){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                    handleSquareClick(square)
                } else {
                    val destinationSquare = square
                    when(selectedSquare!!.pieceType){
                        PieceType.PAWN -> {
                            val pawn = Pawn(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (pawn.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }
                        else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                    }
                }
            }

            !isWhiteStarting && square.pieceColor == PieceColor.BLACK && selectedSquare == null -> { //first click as black
                removeHighlightCircles()
                removeHighlightOpponents()
                when(square.pieceType){
                    PieceType.PAWN -> {
                        val pawn = Pawn(this, chessboardLayout, chessboard, square)
                        pawn.showHighlightSquares()
                        selectedSquare = square
                    }
                    PieceType.ROOK -> {println("rook")}
                    PieceType.KNIGHT -> {println("knight")}
                    PieceType.BISHOP -> {println("bishop")}
                    PieceType.QUEEN -> {println("queen")}
                    PieceType.KING -> {println("king")}
                    else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                }
            }

            !isWhiteStarting && selectedSquare != null -> { //second click as black
                if (selectedSquare == square){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                } else if (selectedSquare?.pieceColor == square.pieceColor){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                    handleSquareClick(square)
                } else {
                    val destinationSquare = square
                    when(selectedSquare!!.pieceType){
                        PieceType.PAWN -> {
                            val pawn = Pawn(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (pawn.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }
                        else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                    }
                }
            }
        }
    }

    private fun movePiece(sourceSquare: Square, destinationSquare: Square) {
        destinationSquare.pieceType = sourceSquare.pieceType
        destinationSquare.isOccupied = true
        destinationSquare.pieceColor = sourceSquare.pieceColor
        removePiece(sourceSquare)
        addPiece(destinationSquare)
    }

    private fun addPiece(square: Square) {
        val pieceImageView = createPieceImageView(square)
        square.imageView = pieceImageView
        square.frameLayout?.addView(pieceImageView)
    }

    private fun removePiece(square: Square) {
        square.frameLayout?.removeView(square.imageView)
        square.clearSquare()
    }

    private fun removeHighlightCircles() {
        val circlesToRemove = mutableListOf<View>()
        for (i in 0 until chessboardLayout.childCount) {
            val view = chessboardLayout.getChildAt(i)
            if (view.tag == highlightCircleTag) {
                circlesToRemove.add(view)
            }
        }

        circlesToRemove.forEach { circle ->
            chessboardLayout.removeView(circle)
        }
    }

    private fun removeHighlightOpponents() {
        val opponentHighlightsToRemove = mutableListOf<View>()
        for (i in 0 until chessboardLayout.childCount) {
            val view = chessboardLayout.getChildAt(i)
            if (view.tag == highlightOpponentTag) {
                opponentHighlightsToRemove.add(view)
            }
        }

        opponentHighlightsToRemove.forEach { highlight ->
            chessboardLayout.removeView(highlight)
        }
    }
}