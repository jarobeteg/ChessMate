package com.example.chessmate.ui.activity

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
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.Puzzle
import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.PuzzleSolvedDialogFragment
import com.example.chessmate.util.chess.bitboard.BitCell
import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardManager
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.google.android.material.bottomnavigation.BottomNavigationView

class PuzzleLoaderActivity : AbsThemeActivity(), PuzzleSolvedDialogFragment.OnNextPuzzleButtonListener {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var board: Bitboard
    private lateinit var moveGenerator: BitboardMoveGenerator
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var chessThemeUtil: ChessThemeUtil
    private var lightSquareColor = R.color.default_light_square_color
    private var darkSquareColor = R.color.default_dark_square_color
    private var pieceThemeArray = IntArray(12)
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var solution: MutableList<BitMove>
    private lateinit var puzzles: ArrayList<Puzzle>
    private lateinit var currentPuzzle: Puzzle
    private var currentIndex: Int = 0
    private var squareSize: Int = 0
    private var isPlayerWhite: Boolean = true
    private var isPlayerTurn: Boolean = true
    private var selectedSquare: BitSquare? = null
    private var availablePlayerMoves = mutableListOf<BitMove>()
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private val highlightMoveTag = "highlight_move"
    private val highlightSelectedTag = "highlight_selected_square"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_loader)

        puzzles = intent.getParcelableArrayListExtra("puzzleList") ?: ArrayList()
        currentIndex = intent.getIntExtra("currentIndex", 0)
        currentPuzzle = puzzles[currentIndex]
        solution = parseSolution(currentPuzzle.solution)

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
        moveGenerator = BitboardMoveGenerator(board)
        uiMapper = BitboardUIMapper()

        displayPuzzle()

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun handleSquareClick(square: BitSquare) {
        when {
            isFirstSelection(square) -> handleFirstSelection(square)
            isDeselecting(square) -> handleDeselect()
            isNewSelection(square) -> handleNewSelection(square)
            isSecondClick() -> handleSecondClick(square)
            else -> {}
        }
    }

    private fun isFirstSelection(square: BitSquare): Boolean {
        return isPlayerTurn && square.color == GameContext.playerColor && selectedSquare == null
    }

    private fun handleFirstSelection(square: BitSquare) {
        removeHighlightsFromSquares()
        processFirstClick(square)
    }

    private fun isDeselecting(square: BitSquare): Boolean {
        return isPlayerTurn && selectedSquare != null && selectedSquare == square
    }

    private fun handleDeselect() {
        removeHighlightsFromSquares()
        selectedSquare = null
    }

    private fun isNewSelection(square: BitSquare): Boolean {
        return isPlayerTurn && square.color == GameContext.playerColor && selectedSquare != null
    }

    private fun handleNewSelection(square: BitSquare) {
        removeHighlightsFromSquares()
        processFirstClick(square)
    }

    private fun isSecondClick(): Boolean {
        return isPlayerTurn && selectedSquare != null
    }

    private fun handleSecondClick(square: BitSquare) {
        if (isCorrectMove(square)) {
            processSecondClick(getCorrectMove())
        } else {
            showIncorrectMoveText()
            removeHighlightMoves()
            removeHighlightsFromSquares()
            selectedSquare = null
        }
    }

    private fun processSecondClick(move: BitMove){
        board.movePiece(move)
        removeMoveFromSolution()

        removeHighlightMoves()
        removeHighlightsFromSquares()
        selectedSquare = null

        addHighlightMove(move.from)
        addHighlightMove(move.to)
        updateBitboardStateUI()
        if (isPuzzleSolved()){
            showPuzzleSolvedDialog()
            isPlayerTurn = false
            return
        }
        isPlayerTurn = false
        makeBotMove()
    }

    private fun processFirstClick(square: BitSquare) {
        availablePlayerMoves.clear()
        val legalMoves = moveGenerator.generateLegalMovesForAlphaBeta(isPlayerWhite)
        for (move in legalMoves) {
            val decodedMove = BitboardMoveGenerator.decodeMove(move)
            if (square.position == decodedMove.from) {
                availablePlayerMoves.add(decodedMove)
            }
        }
        onPlayerMoveCalculated(square)
    }

    private fun makeBotMove() {
        val botMove = getCorrectMove()
        board.movePiece(botMove)
        removeMoveFromSolution()

        removeHighlightMoves()
        addHighlightMove(botMove.from)
        addHighlightMove(botMove.to)
        updateBitboardStateUI()

        isPlayerTurn = true
    }

    private fun onPlayerMoveCalculated(square: BitSquare) {
        if (availablePlayerMoves.isEmpty()){
            if (board.isPlayerInCheck()) {
                addHighlightCheck(board.getKing(GameContext.playerColor))
            }
            selectedSquare = null
            return
        }
        removeHighlightsFromSquares()
        for (move in availablePlayerMoves) {
            if (move.capturedPiece != BitPiece.NONE) {
                addHighlightOpponent(move.to, move.capturedPiece)
            } else {
                addHighlightSquare(move.to)
            }
        }
        selectedSquare = square
        addHighlightSelectedSquare(square.position)
    }

    private fun isCorrectMove(square: BitSquare): Boolean {
        val correctMove = getCorrectMove()
        return correctMove.from == selectedSquare!!.position && correctMove.to == square.position &&
                correctMove.piece == selectedSquare!!.piece && correctMove.capturedPiece == square.piece
    }

    private fun getCorrectMove(): BitMove {
        return solution.first()
    }

    private fun removeMoveFromSolution() {
        solution.removeFirst()
    }

    private fun isPuzzleSolved(): Boolean {
        return solution.isEmpty()
    }

    private fun showPuzzleSolvedDialog() {
        val dialog = PuzzleSolvedDialogFragment()
        dialog.show(supportFragmentManager, "PuzzleSolvedDialog")
    }

    override fun onNextPuzzleButtonListener() {
        nextPuzzle()
    }

    private fun parseSolution(solution: String): MutableList<BitMove> {
        val moveSegments = solution.split("|").map { it.trim() }
        val moves = mutableListOf<BitMove>()

        moveSegments.forEach { segment ->
            val parts = segment.split(" ")
            if (parts.size == 4) {
                val fromCell = getBitCell(parts[0])
                val toCell = getBitCell(parts[1])
                val piece = BitPiece.fromOrdinal(parts[2].toInt())
                val capturedPiece = BitPiece.fromOrdinal(parts[3].toInt())

                val move = BitMove(from = fromCell, to = toCell, piece = piece, capturedPiece = capturedPiece)
                moves.add(move)
            }
        }
        return moves
    }

    private fun getBitCell(cell: String): Long {
        return BitCell.valueOf(cell).bit
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
        isPlayerTurn = true
        currentPuzzle = puzzles[currentIndex]
        solution = parseSolution(currentPuzzle.solution)
        val fen = FEN(currentPuzzle.fen)
        isPlayerWhite = fen.activeColor == 'w'
        GameContext.playerColor = if (isPlayerWhite) PieceColor.WHITE else PieceColor.BLACK
        GameContext.botColor = if (isPlayerWhite) PieceColor.BLACK else PieceColor.WHITE
        setupTextViews()

        chessboardLayout.removeAllViews()
        board.setupFENPosition(fen)
        initializeBitboardUI()
        setupInitialBoardUI()
        setupSquareListener()
    }

    private fun setupTextViews() {
        val title = findViewById<TextView>(R.id.puzzle_loader_toolbar_title)
        val difficultyTextView = findViewById<TextView>(R.id.current_puzzle_difficulty)
        val puzzleIDTextView = findViewById<TextView>(R.id.current_puzzle_id)

        if (isPlayerWhite) {
            val white = getString(R.string.for_white)
            title.text = getString(R.string.find_the_best_move, white)
        } else {
            val black = getString(R.string.for_black)
            title.text = getString(R.string.find_the_best_move, black)
        }

        difficultyTextView.text = when (currentPuzzle.difficulty) {
            1 -> getString(R.string.beginner_level)
            2 -> getString(R.string.intermediate_level)
            3 -> getString(R.string.advanced_level)
            else -> ""
        }

        val result = getString(R.string.puzzle_text) + " " + currentPuzzle.puzzleId.toString()
        puzzleIDTextView.text = result
    }

    private fun removeHighlightsFromSquares() {
        for (row in 0..7) {
            for (col in 0..7) {
                val frameLayout = uiSquares[row][col]
                val highlightToRemove = mutableListOf<View>()

                for (i in 0 until frameLayout.childCount) {
                    val view = frameLayout.getChildAt(i)
                    if (view.tag == highlightCircleTag || view.tag == highlightOpponentTag || view.tag == highlightSelectedTag) {
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

    private fun addHighlightOpponent(position: Long, opponentPiece: BitPiece) {
        val pos = BitboardManager.positionToRowCol(position)
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

    private fun addHighlightSelectedSquare(position: Long) {
        val pos = BitboardManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val squareImageView = squareFrameLayout.findViewWithTag<ImageView>("pieceImageView")
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_selected_square)
        imageView.tag = highlightSelectedTag
        squareFrameLayout.removeView(squareImageView)
        squareFrameLayout.addView(imageView)
        squareFrameLayout.addView(squareImageView)
    }

    private fun addHighlightSquare(position: Long) {
        val pos = BitboardManager.positionToRowCol(position)
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

    private fun addHighlightMove(position: Long) {
        val pos = BitboardManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout.addView(imageView)

    }

    private fun addHighlightCheck(position: Long){
        val pos = BitboardManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
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

    private fun showIncorrectMoveText() {
        Toast.makeText(this, getString(R.string.incorrect_solution), Toast.LENGTH_SHORT).show()
    }

    private fun previousPuzzle() {
        if (currentIndex > 0) {
            currentIndex--
            displayPuzzle()
        } else {
            currentIndex = puzzles.size - 1
            displayPuzzle()
        }
    }

    private fun puzzleHint() {

    }

    private fun nextPuzzle() {
        if (currentIndex < puzzles.size - 1) {
            currentIndex++
            displayPuzzle()
        } else {
            currentIndex = 0
            displayPuzzle()
        }
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.previous_puzzle -> {
                previousPuzzle()
                return true
            }

            R.id.puzzle_hint -> {
                puzzleHint()
                return true
            }

            R.id.next_puzzle -> {
                nextPuzzle()
                return true
            }

            else -> return false
        }
    }
}