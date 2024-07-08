package com.example.chessmate.ui.activity

import android.os.Bundle
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
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardListener
import com.example.chessmate.util.chess.bitboard.BitboardManager
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.google.android.material.bottomnavigation.BottomNavigationView

class BitboardActivity : AbsThemeActivity(), BitboardListener {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var gameManager: BitboardManager
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var turnNumber: TextView
    private lateinit var whiteLastMove: TextView
    private lateinit var blackLastMove: TextView
    private var isPlayerStarted: Boolean = true
    private var squareSize: Int = 0
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private val highlightMoveTag = "highlight_move"
    private var selectedSquare: BitSquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bitboard_bottom_navigation)

        gameManager = BitboardManager(this)
        chessboardLayout = findViewById(R.id.bitboard)

        uiMapper = BitboardUIMapper()

        turnNumber = findViewById(R.id.bitboard_turn_number)
        whiteLastMove = findViewById(R.id.bitboard_white_last_move)
        blackLastMove = findViewById(R.id.bitboard_black_last_move)

        initializeBitboardUI()
        gameManager.initializeUIAndSquareListener(isPlayerStarted)

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    override fun onStart() {
        super.onStart()
        gameManager.startGame()
    }

    private fun initializeBitboardUI() {
        val screenSize = resources.displayMetrics.widthPixels
        squareSize = screenSize / 8

        uiSquares = Array(8) { row ->
            Array(8) { col ->
                val squareLayout = FrameLayout(this).apply {
                    layoutParams = ViewGroup.LayoutParams(squareSize, squareSize)
                }
                chessboardLayout.addView(squareLayout)
                val position = if (isPlayerStarted) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                uiMapper.addSquare(position, squareLayout)
                squareLayout
            }
        }

        if (!isPlayerStarted) {
            uiSquares = Array(8) { row ->
                Array(8) { col ->
                    uiSquares[7 - row][7 - col]
                }
            }
        }
    }

    override fun setupInitialBoardUI(bitboard: Bitboard) {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerStarted) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val square = bitboard.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                updateSquareUI(square, squareLayout)
                setupSquareColors(Position(row, col), squareLayout)
                addRowAndColumnIdentifiers(Position(row, col), squareLayout)
            }
        }
    }

    override fun setupSquareListener(bitboard: Bitboard) {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerStarted) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val squareLayout = uiMapper.getSquareView(position)
                squareLayout.setOnClickListener {
                    handleSquareClick(gameManager.getBitPiece(position))
                }
            }
        }
    }

    private fun updateSquareUI(square: BitSquare, frameLayout: FrameLayout) {
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

    private fun setupSquareColors(position: Position, frameLayout: FrameLayout) {
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color
        val row = position.row
        val col = position.col
        val colorResId = if ((row + col) % 2 == 0) lightSquareColor else darkSquareColor
        frameLayout.setBackgroundResource(colorResId)
    }

    private fun addRowAndColumnIdentifiers(position: Position, frameLayout: FrameLayout) {
        val row = position.row
        val col = position.col

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

    private fun getPieceResourceId(piece: BitPiece): Int {
        return when (piece) {
            BitPiece.WHITE_PAWN -> R.drawable.piece_default_pawn_white
            BitPiece.WHITE_KNIGHT -> R.drawable.piece_default_knight_white
            BitPiece.WHITE_BISHOP -> R.drawable.piece_default_bishop_white
            BitPiece.WHITE_ROOK -> R.drawable.piece_default_rook_white
            BitPiece.WHITE_QUEEN -> R.drawable.piece_default_queen_white
            BitPiece.WHITE_KING -> R.drawable.piece_default_king_white
            BitPiece.BLACK_PAWN -> R.drawable.piece_default_pawn_black
            BitPiece.BLACK_KNIGHT -> R.drawable.piece_default_knight_black
            BitPiece.BLACK_BISHOP -> R.drawable.piece_default_bishop_black
            BitPiece.BLACK_ROOK -> R.drawable.piece_default_rook_black
            BitPiece.BLACK_QUEEN -> R.drawable.piece_default_queen_black
            BitPiece.BLACK_KING -> R.drawable.piece_default_king_black
            BitPiece.NONE -> 0
        }
    }

    private fun handleSquareClick(square: BitSquare) {
        val pos = gameManager.positionToRowCol(square.position)
        println("position $pos, handleSquareClick: $square")
        if (gameManager.isPlayerTurn && square.color == gameManager.playerColor() && selectedSquare == null) {
            removeHighlightOpponentsAndSquares()
            gameManager.processFirstClick(square)
        } else if (gameManager.isPlayerTurn && selectedSquare != null && selectedSquare == square) {
            removeHighlightOpponentsAndSquares()
            selectedSquare = null
        } else if (gameManager.isPlayerTurn && selectedSquare != null && square.color == gameManager.playerColor()) {
            removeHighlightOpponentsAndSquares()
            gameManager.processFirstClick(square)
        } else if (gameManager.isPlayerTurn && selectedSquare != null) {
            gameManager.processSecondClick(square)
        }
    }

    override fun onPlayerMoveCalculated(moves: MutableList<BitMove>, square: BitSquare) {
        removeHighlightOpponentsAndSquares()
        for (move in moves) {
            if (move.capturedPiece != BitPiece.NONE) {
                addHighlightOpponent(move.to, move.capturedPiece)
            } else {
                addHighlightSquare(move.to)
            }
        }
        selectedSquare = square
    }

    override fun onPlayerMoveMade(bitboard: Bitboard, move: BitMove) {
        removeHighlightMoves()
        removeHighlightOpponentsAndSquares()
        selectedSquare = null
        addHighlightMove(move.from)
        addHighlightMove(move.to)
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerStarted) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val square = bitboard.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                updateSquareUI(square, squareLayout)
            }
        }
    }

    private fun addHighlightSquare(position: Long) {
        val pos = gameManager.positionToRowCol(position)
        val frameLayout = uiSquares[pos.row][pos.col]
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

    private fun addHighlightOpponent(position: Long, opponentPiece: BitPiece) {
        val pos = gameManager.positionToRowCol(position)
        if (opponentPiece.name.contains("KING")) return
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val squareImageView = squareFrameLayout.findViewWithTag<ImageView>("pieceImageView")
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag
        squareFrameLayout.removeView(squareImageView)
        squareFrameLayout.addView(imageView)
        squareFrameLayout.addView(squareImageView)
    }

    private fun addHighlightMove(position: Long) {
        val pos = gameManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout.addView(imageView)

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

    private fun removeHighlightMoves() {
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
}