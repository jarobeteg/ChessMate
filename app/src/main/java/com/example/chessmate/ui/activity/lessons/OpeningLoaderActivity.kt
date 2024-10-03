package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.LessonCompletionRepository
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.LessonCompletion
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.OpeningRepo
import com.example.chessmate.util.UserProfileManager
import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitCell
import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardManager
import com.example.chessmate.util.chess.bitboard.BitboardMoveGenerator
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.example.chessmate.util.chess.bitboard.BoardStateTracker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class OpeningLoaderActivity : AbsThemeActivity() {
    private lateinit var lessonCompletionRepository: LessonCompletionRepository
    private lateinit var userProfileRepository: UserProfileRepository
    private var userProfile: UserProfile? = null
    private val userProfileManager = UserProfileManager.getInstance()
    private lateinit var chessboardLayout: GridLayout
    private lateinit var board: Bitboard
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var chessThemeUtil: ChessThemeUtil
    private var lightSquareColor = R.color.default_light_square_color
    private var darkSquareColor = R.color.default_dark_square_color
    private var pieceThemeArray = IntArray(12)
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var solution: MutableList<BitMove>
    private lateinit var openingRepos: ArrayList<OpeningRepo>
    private lateinit var currentOpeningRepo: OpeningRepo
    private lateinit var openingTitle: TextView
    private lateinit var openingDescriptionTitle: TextView
    private lateinit var openingDescription: TextView
    private lateinit var flipBoard: ImageButton
    private var boardStateIndex: Int = 0
    private var currentIndex: Int = 0
    private var textIndex: Int = 0
    private var squareSize: Int = 0
    private var isPlayerWhite: Boolean = true
    private val highlightMoveTag = "highlight_move"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opening_loader)

        userProfileRepository = UserProfileRepository(this)
        lessonCompletionRepository = LessonCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        openingRepos = intent.getParcelableArrayListExtra("openingRepos") ?: ArrayList()
        currentIndex = intent.getIntExtra("currentIndex", 0)
        currentOpeningRepo = openingRepos[currentIndex]
        solution = parseSolution(currentOpeningRepo.solution)

        bottomNavigationView = findViewById(R.id.opening_loader_bottom_navigation)
        openingTitle = findViewById(R.id.opening_loader_toolbar_title)
        openingDescriptionTitle = findViewById(R.id.opening_description_title)
        openingDescription = findViewById(R.id.opening_description)
        flipBoard = findViewById(R.id.flip_board)

        toolbar = findViewById(R.id.opening_loader_toolbar)
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

        displayOpening()

        flipBoard.setOnClickListener { flipBoard() }

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun displayOpening() {
        board.clearBoardStateTracker()
        currentOpeningRepo = openingRepos[currentIndex]
        solution = parseSolution(currentOpeningRepo.solution)
        val fen = FEN(currentOpeningRepo.fen)
        isPlayerWhite = fen.activeColor == 'w'
        openingTitle.text = currentOpeningRepo.title

        chessboardLayout.removeAllViews()
        board.setupFENPosition(fen)
        initializeBitboardUI()
        setupInitialBoardUI()

        preMoveSolution()
        boardStateIndex = 0
        textIndex = 0
    }

    private fun flipBoard() {
        isPlayerWhite = !isPlayerWhite
        chessboardLayout.removeAllViews()
        initializeBitboardUI()
        setupInitialBoardUI()
        displayBoardState(board.stateTracker[boardStateIndex])
    }

    private fun preMoveSolution() {
        for (move in solution) {
            board.movePiece(move)
        }
    }

    private fun updateTextViews(descriptionTitleId: Int, descriptionId: Int) {
        openingDescriptionTitle.text = getDescriptionTitleById(descriptionTitleId)
        openingDescription.text = getDescriptionById(descriptionId)
    }

    private fun getDescriptionTitleById(id: Int): String {
        val resourceName = "description_title_$id"
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) getString(resourceId) else getString(R.string.string_not_found)
    }

    private fun getDescriptionById(id: Int): String {
        val resourceName = "description_$id"
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) getString(resourceId) else getString(R.string.string_not_found)
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

                val move = BitMove (
                    from = fromCell,
                    to = toCell,
                    piece = piece,
                    capturedPiece = capturedPiece
                )
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


    private fun addHighlightMove(position: Long) {
        val pos = BitboardManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout.addView(imageView)

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

    private fun previousOpening() {
        if (currentIndex > 0) {
            currentIndex--
            displayOpening()
        } else {
            currentIndex = openingRepos.size - 1
            displayOpening()
        }
    }

    private fun nextOpening() {
        if (currentIndex < openingRepos.size - 1) {
            currentIndex++
            displayOpening()
        } else {
            currentIndex = 0
            displayOpening()
        }
    }

    private fun previousStep() {
        if (textIndex > 0) {
            textIndex--
            updateTextViews(currentOpeningRepo.descriptionTitleId[textIndex], currentOpeningRepo.descriptionId[textIndex])
        } else {
            openingDescriptionTitle.text = ""
            openingDescription.text = ""
        }

        if (boardStateIndex > 0) {
            boardStateIndex--
            displayBoardState(board.stateTracker[boardStateIndex])
        }
    }

    private fun nextStep() {
        if (textIndex < currentOpeningRepo.descriptionId.size - 1) {
            updateTextViews(currentOpeningRepo.descriptionTitleId[textIndex], currentOpeningRepo.descriptionId[textIndex])
            textIndex++
        } else {
            updateTextViews(currentOpeningRepo.descriptionTitleId.last(), currentOpeningRepo.descriptionId.last())
        }

        if (boardStateIndex < board.stateTracker.size - 1) {
            boardStateIndex++
            displayBoardState(board.stateTracker[boardStateIndex])
            if (isOpeningFinished()) {
                lifecycleScope.launch {
                    updateDatabase()
                }
                displayOpeningFinishedTitle()
            }
        }
    }

    private fun displayBoardState(state: BoardStateTracker) {
        removeHighlightMoves()
        board.updateBoardState(state)
        val lastMove = state.move
        val decodeMove = BitboardMoveGenerator.decodeMove(lastMove)
        if (boardStateIndex != 0) {
            addHighlightMove(decodeMove.from)
            addHighlightMove(decodeMove.to)
        }
        updateBitboardStateUI()
    }

    private fun isOpeningFinished(): Boolean {
        return boardStateIndex == board.stateTracker.size - 1
    }

    private fun displayOpeningFinishedTitle() {
        openingTitle.text = getString(R.string.opening_finished_text)
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.previous_opening -> {
                previousOpening()
                return true
            }

            R.id.previous_step -> {
                previousStep()
                return true
            }

            R.id.next_step -> {
                nextStep()
                return true
            }

            R.id.next_opening -> {
                nextOpening()
                return true
            }

            else -> return false
        }
    }

    private suspend fun updateDatabase() {
        if (userProfile == null) return

        val existingCompletion = lessonCompletionRepository.isOpeningFinished(userProfile!!.userID, currentOpeningRepo.lessonId)
        userProfileRepository.incrementLessonTaken(userProfile!!.userID)

        if (existingCompletion != null) {
         return
        }

        val openingCompletion = LessonCompletion (
            userID = userProfile!!.userID,
            type = 3,
            lessonID = currentOpeningRepo.lessonId,
            subLessonID = 0,
            isSolved = true
        )

        lessonCompletionRepository.insertLessonCompletion(openingCompletion)
    }
}