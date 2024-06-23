package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.chessmate.R
import com.example.chessmate.util.chess.ChessGameManager
import com.example.chessmate.util.chess.ChessGameListener
import com.example.chessmate.util.chess.Chessboard
import com.example.chessmate.util.chess.Move
import com.example.chessmate.util.chess.Piece
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.PieceType
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.PromotionDialogFragment
import com.example.chessmate.util.chess.Square
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChessGameActivity : AbsThemeActivity(), PromotionDialogFragment.PromotionDialogListener, ChessGameListener {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var gameManager: ChessGameManager
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var turnNumber: TextView
    private lateinit var whiteLastMove: TextView
    private lateinit var blackLastMove: TextView
    private var isPlayerStarted = false
    private var depth = 1
    private var squareSize: Int = 0
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private val highlightMoveTag = "highlight_move"
    private var selectedSquare: Square? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_game)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        //this code snippet determines the user starting side
        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        var startingSide = sharedPreferences.getString("starting_side", "random")
        depth += sharedPreferences.getInt("depth", 0)
        if (startingSide == "random") {
            val random = java.util.Random()
            val isWhite = random.nextBoolean()
            startingSide = if (isWhite) "white" else "black"
        }

        //the chessboard gets initialized with empty squares and square sizes are determined based on the device's display metrics
        gameManager = ChessGameManager(this)
        chessboardLayout = findViewById(R.id.chessboard)

        uiSquares = Array(8) { row ->
            Array(8) { col ->
                findViewById(resources.getIdentifier("square_${row}${col}", "id", packageName))
            }
        }

        turnNumber = findViewById(R.id.turn_number)
        whiteLastMove = findViewById(R.id.white_last_move)
        blackLastMove = findViewById(R.id.black_last_move)

        initializeChessboardUI()
        //here the the chessboard gets set up with a starting position based on which color starts
        if (startingSide == "white"){
            isPlayerStarted = true
            gameManager.initializeUIAndSquareListener(true)
        }else{
            isPlayerStarted = false
            gameManager.initializeUIAndSquareListener(false)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    override fun onStart() {
        super.onStart()
        gameManager.startGame()
    }

    private fun initializeChessboardUI() {
        val screenSize = resources.displayMetrics.widthPixels
        squareSize = screenSize / 8

        uiSquares = Array(8) {
            Array(8) {
                val squareLayout = FrameLayout(this).apply {
                    layoutParams = ViewGroup.LayoutParams(squareSize, squareSize)
                }
                chessboardLayout.addView(squareLayout)
                squareLayout
            }
        }
    }

    override fun setupInitialBoardUI(chessboard: Chessboard) {
        for (row in 0..7) {
            for (col in 0..7) {
                val square = chessboard.getSquare(row, col)
                updateSquareUI(square, uiSquares[row][col])
                setupSquareColors(square, uiSquares[row][col])
                addRowAndColumnIdentifiers(square, uiSquares[row][col])
            }
        }
    }

    override fun setupSquareListener(chessboard: Chessboard) {
        for (row in 0..7) {
            for (col in 0..7) {
                uiSquares[row][col].setOnClickListener {
                    val square = chessboard.getSquare(row, col)
                    handleSquareClick(square)
                }
            }
        }
    }

    private fun updateSquareUI(square: Square, frameLayout: FrameLayout) {
        val existingPieceImageView = frameLayout.findViewWithTag<ImageView>("pieceImageView")
        existingPieceImageView?.let {
            frameLayout.removeView(it)
        }

        val pieceImageView = ImageView(this)
        pieceImageView.tag = "pieceImageView"
        val resourceId = getPieceResourceId(square.piece)
        pieceImageView.setImageResource(resourceId)
        frameLayout.addView(pieceImageView)
    }

    private fun setupSquareColors(square: Square, frameLayout: FrameLayout) {
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color
        val row = square.row
        val col = square.col
        val colorResId = if ((row + col) % 2 == 0) lightSquareColor else darkSquareColor
        frameLayout.setBackgroundResource(colorResId)
    }

    private fun addRowAndColumnIdentifiers(square: Square, frameLayout: FrameLayout) {
        val row = square.row
        val col = square.col

        if (col == 0) {
            val numberTextView = TextView(this)
            val number = if (isPlayerStarted) (8 - row).toString() else (row + 1).toString()
            numberTextView.text = number
            val textSizeInSp = 10
            numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat())
            numberTextView.setTextColor(getNumberTextColor(row))
            numberTextView.setPadding(2,0,0,0)
            numberTextView.gravity = Gravity.START or Gravity.TOP
            numberTextView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            frameLayout.addView(numberTextView)
        }

        if (row == 7) {
            val letterTextView = TextView(this)
            val letter = if (isPlayerStarted) ('a' + col).toString() else ('h' - col).toString()
            letterTextView.text = letter
            val textSizeInSp = 10
            letterTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat())
            letterTextView.setTextColor(getLetterTextColor(col))
            letterTextView.setPadding(0,0,2,0)
            letterTextView.gravity = Gravity.END or Gravity.BOTTOM
            letterTextView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            frameLayout.addView(letterTextView)
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

    private fun getPieceResourceId(piece: Piece): Int {
        return when (piece.type) {
            PieceType.PAWN -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_pawn_white else R.drawable.piece_default_pawn_black
            PieceType.KNIGHT -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_knight_white else R.drawable.piece_default_knight_black
            PieceType.BISHOP -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_bishop_white else R.drawable.piece_default_bishop_black
            PieceType.ROOK -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_rook_white else R.drawable.piece_default_rook_black
            PieceType.QUEEN -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_queen_white else R.drawable.piece_default_queen_black
            PieceType.KING -> if (piece.color == PieceColor.WHITE) R.drawable.piece_default_king_white else R.drawable.piece_default_king_black
            PieceType.NONE -> 0
        }
    }

    private fun handleSquareClick(square: Square) {
        println("clicked square data: $square")
        if (gameManager.isPlayerTurn && square.piece.color == gameManager.playerColor() && selectedSquare == null) {
            removeHighlightOpponentsAndSquares()
            gameManager.processFirstClick(square)
        } else if (gameManager.isPlayerTurn && selectedSquare != null && selectedSquare == square) {
            removeHighlightOpponentsAndSquares()
            selectedSquare = null
        } else if (gameManager.isPlayerTurn && selectedSquare != null && square.piece.color == gameManager.playerColor()) {
            removeHighlightOpponentsAndSquares()
            selectedSquare = null
            handleSquareClick(square)
        } else if (gameManager.isPlayerTurn && selectedSquare != null) {
            gameManager.processSecondClick(square)
        }
    }

    override fun onMoveMade(chessboard: Chessboard) {
        for (row in 0..7) {
            for (col in 0..7) {
                val square = chessboard.getSquare(row, col)
                updateSquareUI(square, uiSquares[row][col])
            }
        }
    }

    override fun onPlayerMoveMade(chessboard: Chessboard) {
        removeHighlightOpponentsAndSquares()
        selectedSquare = null

        for (row in 0..7) {
            for (col in 0..7) {
                val square = chessboard.getSquare(row, col)
                updateSquareUI(square, uiSquares[row][col])
            }
        }
    }

    override fun onPlayerMoveCalculated(moves: MutableList<Move>, square: Square) {
        for (move in moves) {
            if (move.capturedPiece != null) {
                addHighlightOpponent(move.to, move.capturedPiece.type)
            } else {
                addHighlightSquare(move.to.row, move.to.col)
            }
        }
        selectedSquare = square
    }

    override fun updateMoveHighlights(from: Position, to: Position) {
        removeMoveHighlights()
        addMoveHighlights(from)
        addMoveHighlights(to)
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.nav_resign -> {
                return true
            }

            R.id.nav_back -> {
                return true
            }

            R.id.nav_forward -> {
                return true
            }

            R.id.nav_continue -> {
                return true
            }

            else -> return false
        }
    }

    private fun addHighlightSquare(row: Int, col: Int) {
        val frameLayout = uiSquares[row][col]
        val squareSize = chessboardLayout.width / 8
        val circleSize = squareSize / 3

        val circleParams = FrameLayout.LayoutParams(circleSize, circleSize).apply {
            gravity = Gravity.CENTER
        }

        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.highlight_square_circle)
            tag = highlightCircleTag
        }

        frameLayout.addView(imageView, circleParams)
    }

    private fun addHighlightOpponent(position: Position, opponentPieceType: PieceType) {
        val row = position.row
        val col = position.col
        if (opponentPieceType == PieceType.KING) return
        val squareFrameLayout = uiSquares[row][col]
        val squareImageView = squareFrameLayout.findViewWithTag<ImageView>("pieceImageView")
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag
        squareFrameLayout.removeView(squareImageView)
        squareFrameLayout.addView(imageView)
        squareFrameLayout.addView(squareImageView)
    }

    private fun addMoveHighlights(position: Position){
        val squareFrameLayout = uiSquares[position.row][position.col]
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout.addView(imageView)
    }

    private fun addHighlightCheck(row: Int, col: Int){
        val squareFrameLayout = uiSquares[row][col]
        val squareImageView = squareFrameLayout.findViewWithTag<ImageView>("pieceImageView")
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag

        squareFrameLayout.removeView(squareImageView)
        squareFrameLayout.addView(imageView)
        squareFrameLayout.addView(squareImageView)

        val countdownTimer = object : CountDownTimer(3000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                when (millisUntilFinished){
                    in 0..500 -> imageView.visibility = View.INVISIBLE
                    in 501..1000 -> imageView.visibility = View.VISIBLE
                    in 1001..1500 -> imageView.visibility = View.INVISIBLE
                    in 1501..2000 -> imageView.visibility = View.VISIBLE
                    in 2001..2500 -> imageView.visibility = View.INVISIBLE
                    in 2501..3000 -> imageView.visibility = View.VISIBLE
                }
            }

            override fun onFinish() {
                squareFrameLayout.removeView(imageView)
            }
        }

        countdownTimer.start()
    }

    private fun removeMoveHighlights(){
        for (row in 0..7) {
            for (col in 0..7) {
                val frameLayout = uiSquares[row][col]
                val highlightToRemove = mutableListOf<View>()

                for (i in 0 until frameLayout.childCount) {
                    val view = frameLayout.getChildAt(i)
                    if (view.tag == highlightMoveTag) {
                        highlightToRemove.add(view)
                    }
                }

                highlightToRemove.forEach { highlight ->
                    frameLayout.removeView(highlight)
                }
            }
        }
    }

    private fun removeHighlightOpponentsAndSquares() {
        for (row in 0..7) {
            for (col in 0..7) {
                val frameLayout = uiSquares[row][col]
                val highlightToRemove = mutableListOf<View>()

                for (i in 0 until frameLayout.childCount) {
                    val view = frameLayout.getChildAt(i)
                    if (view.tag == highlightCircleTag || view.tag == highlightOpponentTag) {
                        highlightToRemove.add(view)
                    }
                }

                highlightToRemove.forEach { highlight ->
                    frameLayout.removeView(highlight)
                }
            }
        }
    }

    override fun onPieceSelected(pieceType: PieceType, destinationSquare: Square) {
        when(pieceType){
            PieceType.QUEEN -> {}
            PieceType.ROOK -> {}
            PieceType.BISHOP -> {}
            PieceType.KNIGHT -> {}
            else -> throw IllegalArgumentException("Unexpected PieceType: $pieceType")
        }
    }
}