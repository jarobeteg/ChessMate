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
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.Puzzle
import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.google.android.material.bottomnavigation.BottomNavigationView

class PuzzleLoaderActivity : AbsThemeActivity() {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var board: Bitboard
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var chessThemeUtil: ChessThemeUtil
    private var lightSquareColor = R.color.default_light_square_color
    private var darkSquareColor = R.color.default_dark_square_color
    private var pieceThemeArray = IntArray(12)
    private var isPlayerWhite: Boolean = true
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var puzzles: ArrayList<Puzzle>
    private var currentIndex: Int = 0
    private var squareSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_loader)

        puzzles = intent.getParcelableArrayListExtra("puzzleList") ?: ArrayList()
        currentIndex = intent.getIntExtra("currentIndex", 0)

        bottomNavigationView = findViewById(R.id.puzzle_loader_bottom_navigation)

        toolbar = findViewById(R.id.puzzle_loader_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        chessThemeUtil = ChessThemeUtil(this)

        val boardTheme = chessThemeUtil.getBoardTheme()
        lightSquareColor = boardTheme.first
        darkSquareColor = boardTheme.second

        pieceThemeArray = chessThemeUtil.getPieceTheme()

        chessboardLayout = findViewById(R.id.bitboard)
        board = Bitboard()
        uiMapper = BitboardUIMapper()

        displayPuzzle()

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun handleSquareClick(square: BitSquare) {
        println("square clicked: $square")
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
                val position = if (isPlayerWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                uiMapper.addSquare(position, squareLayout)
                squareLayout
            }
        }

        if (!isPlayerWhite) {
            uiSquares = Array(8) { row ->
                Array(8) { col ->
                    uiSquares[7 - row][7 - col]
                }
            }
        }
    }

    private fun setupInitialBoardUI() {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val square = board.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                updateSquareUI(square, squareLayout)
                setupSquareColors(Position(row, col), squareLayout)
                addRowAndColumnIdentifiers(Position(row, col), squareLayout)
            }
        }
    }

    private fun setupSquareListener() {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val squareLayout = uiMapper.getSquareView(position)
                squareLayout.setOnClickListener {
                    handleSquareClick(board.getPiece(position))
                }
            }
        }
    }

    private fun updateBitboardStateUI() {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (isPlayerWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val square = board.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                updateSquareUI(square, squareLayout)
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
            val number = if (isPlayerWhite) (8 - row).toString() else (row + 1).toString()
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
            val letter = if (isPlayerWhite) ('a' + col).toString() else ('h' - col).toString()
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
        return if (row % 2 == 0) getColor(darkSquareColor)
        else getColor(lightSquareColor)
    }

    private fun getLetterTextColor(col: Int): Int {
        return if (col % 2 == 0) getColor(lightSquareColor)
        else getColor(darkSquareColor)
    }

    private fun getPieceResourceId(piece: BitPiece): Int {
        return when (piece) {
            BitPiece.WHITE_PAWN -> pieceThemeArray[0]
            BitPiece.WHITE_KNIGHT -> pieceThemeArray[1]
            BitPiece.WHITE_BISHOP -> pieceThemeArray[2]
            BitPiece.WHITE_ROOK -> pieceThemeArray[3]
            BitPiece.WHITE_QUEEN -> pieceThemeArray[4]
            BitPiece.WHITE_KING -> pieceThemeArray[5]
            BitPiece.BLACK_PAWN -> pieceThemeArray[6]
            BitPiece.BLACK_KNIGHT -> pieceThemeArray[7]
            BitPiece.BLACK_BISHOP -> pieceThemeArray[8]
            BitPiece.BLACK_ROOK -> pieceThemeArray[9]
            BitPiece.BLACK_QUEEN -> pieceThemeArray[10]
            BitPiece.BLACK_KING -> pieceThemeArray[11]
            BitPiece.NONE -> 0
        }
    }

    private fun displayPuzzle() {
        setupTextViews()
        val puzzle = puzzles[currentIndex]
        val fen = FEN(puzzle.fen)
        isPlayerWhite = fen.activeColor == 'w'

        chessboardLayout.removeAllViews()
        board.setupFENPosition(fen)
        initializeBitboardUI()
        setupInitialBoardUI()
        setupSquareListener()
    }

    private fun setupTextViews() {
        val difficultyTextView = findViewById<TextView>(R.id.current_puzzle_difficulty)
        val puzzleIDTextView = findViewById<TextView>(R.id.current_puzzle_id)

        difficultyTextView.text = when (puzzles[currentIndex].difficulty) {
            1 -> getString(R.string.beginner_level)
            2 -> getString(R.string.intermediate_level)
            3 -> getString(R.string.advanced_level)
            else -> ""
        }

        val result = getString(R.string.puzzle_text) + " " + puzzles[currentIndex].puzzleId.toString()
        puzzleIDTextView.text = result
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.previous_puzzle -> {
                if (currentIndex > 0) {
                    currentIndex--
                    displayPuzzle()
                } else {
                    currentIndex = puzzles.size - 1
                    displayPuzzle()
                }
                return true
            }

            R.id.puzzle_hint -> {
                return true
            }

            R.id.next_puzzle -> {
                if (currentIndex < puzzles.size - 1) {
                    currentIndex++
                    displayPuzzle()
                } else {
                    currentIndex = 0
                    displayPuzzle()
                }
                return true
            }

            else -> return false
        }
    }
}