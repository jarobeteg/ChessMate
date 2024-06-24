package com.example.chessmate.ui.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.chessmate.R
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitPiece
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
    private var isPlayerStarted = true
    private var squareSize: Int = 0
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private val highlightMoveTag = "highlight_move"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bitboard_bottom_navigation)

        gameManager = BitboardManager(this)
        chessboardLayout = findViewById(R.id.bitboard)

        uiMapper = BitboardUIMapper(this)

        turnNumber = findViewById(R.id.bitboard_turn_number)
        whiteLastMove = findViewById(R.id.bitboard_white_last_move)
        blackLastMove = findViewById(R.id.bitboard_black_last_move)

        initializeBitboardUI()
        gameManager.initializeUIAndSquareListener()

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
                val position = 1L shl ((7 - row) * 8 + col)
                uiMapper.addSquare(position, squareLayout)
                squareLayout
            }
        }
    }

    override fun setupInitialBoardUI(bitboard: Bitboard) {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = 1L shl ((7 - row) * 8 + col)
                val piece = bitboard.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                updateSquareUI(piece, squareLayout)
                setupSquareColors(Position(row, col), squareLayout)
                addRowAndColumnIdentifiers(Position(row, col), squareLayout)
            }
        }
    }

    override fun setupSquareListener(bitboard: Bitboard) {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = 1L shl ((7 - row) * 8 + col)
                val squareLayout = uiMapper.getSquareView(position)
                squareLayout.setOnClickListener {
                    handleSquareClick(position)
                }
            }
        }
    }

    private fun updateSquareUI(piece: BitPiece, frameLayout: FrameLayout) {
        val existingPieceImageView = frameLayout.findViewWithTag<ImageView>("pieceImageView")
        existingPieceImageView?.let {
            frameLayout.removeView(it)
        }

        val pieceImageView = ImageView(this)
        pieceImageView.tag = "pieceImageView"
        val resourceId = getPieceResourceId(piece)
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

    private fun handleSquareClick(position: Long) {
        val piece = gameManager.getBitPiece(position)
        val squareNotation = gameManager.getSquareNotation(piece.position)
        println("piece: $squareNotation, ${piece.piece}")
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