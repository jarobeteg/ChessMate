package com.example.chessmate.ui.activity.lessons

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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.LessonCompletionRepository
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.LessonCompletion
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.LessonRepo
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

class SubLessonLoaderActivity : AbsThemeActivity() {
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
    private lateinit var lessonRepos: ArrayList<LessonRepo>
    private lateinit var currentLessonRepo: LessonRepo
    private lateinit var lessonTitle: TextView
    private var currentLessonTitle: String = ""
    private var boardStateIndex: Int = 0
    private var currentIndex: Int = 0
    private var squareSize: Int = 0
    private var isPlayerWhite: Boolean = true
    private val highlightMoveTag = "highlight_move"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_lesson_loader)

        userProfileRepository = UserProfileRepository(this)
        lessonCompletionRepository = LessonCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        lessonRepos = intent.getParcelableArrayListExtra("lessonRepos") ?: ArrayList()
        currentIndex = intent.getIntExtra("currentIndex", 0)
        currentLessonTitle = intent.getStringExtra("currentLesson").toString()
        currentLessonRepo = lessonRepos[currentIndex]
        solution = parseSolution(currentLessonRepo.solution)

        bottomNavigationView = findViewById(R.id.sub_lesson_loader_bottom_navigation)
        lessonTitle = findViewById(R.id.sub_lesson_loader_toolbar_title)

        toolbar = findViewById(R.id.sub_lesson_loader_toolbar)
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

        displayLesson()

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun displayLesson() {
        board.clearBoardStateTracker()
        currentLessonRepo = lessonRepos[currentIndex]
        solution = parseSolution(currentLessonRepo.solution)
        val fen = FEN(currentLessonRepo.fen)
        isPlayerWhite = fen.activeColor == 'w'
        setupTextViews()

        chessboardLayout.removeAllViews()
        board.setupFENPosition(fen)
        initializeBitboardUI()
        setupInitialBoardUI()

        preMoveSolution()
        boardStateIndex = 0
    }

    private fun preMoveSolution() {
        for (move in solution) {
            board.movePiece(move)
        }
    }

    private fun setupTextViews() {
        val subLessonTitle = findViewById<TextView>(R.id.current_sub_lesson_title)
        val subLessonDescription = findViewById<TextView>(R.id.sub_lesson_description)

        lessonTitle.text = currentLessonTitle
        subLessonTitle.text = getString(R.string.lesson_num, currentLessonRepo.subLessonId)
        subLessonDescription.text = getDescriptionById(currentLessonRepo.descriptionId)
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
            if (parts.size == 7) {
                val fromCell = getBitCell(parts[0])
                val toCell = getBitCell(parts[1])
                val piece = BitPiece.fromOrdinal(parts[2].toInt())
                val capturedPiece = BitPiece.fromOrdinal(parts[3].toInt())
                val promotion = BitPiece.fromOrdinal(parts[4].toInt())
                val isCastling = parts[5] == "1"
                val isEnPassant = parts[6] == "1"

                val move = BitMove (
                    from = fromCell,
                    to = toCell,
                    piece = piece,
                    capturedPiece = capturedPiece,
                    promotion = promotion,
                    isCastling = isCastling,
                    isEnPassant = isEnPassant
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

    private fun previousLesson() {
        if (currentIndex > 0) {
            currentIndex--
            displayLesson()
        } else {
            currentIndex = lessonRepos.size - 1
            displayLesson()
        }
    }

    private fun nextLesson() {
        if (currentIndex < lessonRepos.size - 1) {
            currentIndex++
            displayLesson()
        } else {
            currentIndex = 0
            displayLesson()
        }
    }

    private fun previousStep() {
        if (boardStateIndex > 0) {
            boardStateIndex--
            displayBoardState(board.stateTracker[boardStateIndex])
        }
    }

    private fun nextStep() {
        if (boardStateIndex < board.stateTracker.size - 1) {
            boardStateIndex++
            displayBoardState(board.stateTracker[boardStateIndex])
            if (isLessonFinished()) {
                lifecycleScope.launch {
                    updateDatabase()
                }
                displayLessonFinishedTitle()
            }
        }
    }

    private fun displayBoardState(state: BoardStateTracker) {
        removeHighlightMoves()
        board.updateBoardState(state)
        val lastMove = state.move
        val decodedMove = BitboardMoveGenerator.decodeMove(lastMove)
        if (boardStateIndex != 0) {
            addHighlightMove(decodedMove.from)
            addHighlightMove(decodedMove.to)
        }
        updateBitboardStateUI()
    }

    private fun isLessonFinished(): Boolean {
        return boardStateIndex == board.stateTracker.size - 1
    }

    private fun displayLessonFinishedTitle() {
        lessonTitle.text = getString(R.string.lesson_finished_text)
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.previous_lesson -> {
                previousLesson()
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

            R.id.next_lesson -> {
                nextLesson()
                return true
            }

            else -> return false
        }
    }

    private suspend fun updateDatabase() {
        if (userProfile == null) return

        val existingCompletion = lessonCompletionRepository.isSubLessonFinished(
            userProfile!!.userID,
            currentLessonRepo.lessonId,
            currentLessonRepo.subLessonId
            )
        userProfileRepository.incrementLessonTaken(userProfile!!.userID)

        if (existingCompletion != null) {
            return
        }

        val lessonCompletion = LessonCompletion (
            userID = userProfile!!.userID,
            type = currentLessonRepo.type,
            lessonID = currentLessonRepo.lessonId,
            subLessonID = currentLessonRepo.subLessonId,
            isSolved = true
        )

        lessonCompletionRepository.insertLessonCompletion(lessonCompletion)
    }
}