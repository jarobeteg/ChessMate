package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.example.chessmate.util.UserProfileManager
import com.example.chessmate.util.chess.CoordinateSettingsDialogFragment
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitCell
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardManager
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Locale

class CoordinatesLessonActivity : AbsThemeActivity(), CoordinateSettingsDialogFragment.OnSaveButtonListener {
    private lateinit var lessonCompletionRepository: LessonCompletionRepository
    private lateinit var userProfileRepository: UserProfileRepository
    private var userProfile: UserProfile? = null
    private val userProfileManager = UserProfileManager.getInstance()
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var countdownTimerTextView: TextView
    private var timeLimit: Long = 15000
    private var timeLeftInMillis: Long = timeLimit
    private var timerRunning = false
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var coordinatesToolbarTitle: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var settings: ImageButton

    private var showCoordinates: Boolean = true
    private var showPieces: Boolean = true
    private var playAsWhite: Boolean = true
    private var isLessonLive: Boolean = false
    private var currentCoordinate = "B4"
    private var taskCounter: Int = 0

    private lateinit var chessboardLayout: GridLayout
    private lateinit var board: Bitboard
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var chessThemeUtil: ChessThemeUtil
    private var lightSquareColor = R.color.default_light_square_color
    private var darkSquareColor = R.color.default_dark_square_color
    private var pieceThemeArray = IntArray(12)
    private var squareSize: Int = 0
    private val highlightSelectedCorrectTag = "highlight_selected_correct_square"
    private val highlightSelectedIncorrectTag = "highlight_selected_incorrect_square"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinates_lesson)

        userProfileRepository = UserProfileRepository(this)
        lessonCompletionRepository = LessonCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        countdownTimerTextView = findViewById(R.id.countdown_timer)
        bottomNavigationView = findViewById(R.id.coordinates_lesson_bottom_navigation)
        coordinatesToolbarTitle = findViewById(R.id.coordinates_lesson_toolbar_title)

        toolbar = findViewById(R.id.coordinates_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        settings = findViewById(R.id.coordinates_lesson_settings)
        settings.setOnClickListener { showSettingsDialog() }

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }

        chessThemeUtil = ChessThemeUtil(this)

        val boardTheme = chessThemeUtil.getBoardTheme()
        lightSquareColor = boardTheme.first
        darkSquareColor = boardTheme.second

        pieceThemeArray = chessThemeUtil.getPieceTheme()

        chessboardLayout = findViewById(R.id.bitboard)
        board = Bitboard()
        uiMapper = BitboardUIMapper()

        updateCountdownText()
        displayCoordinates()
    }

    private fun handleSquareClick(square: BitSquare) {
        if (!isLessonLive) return

        if (square.notation == currentCoordinate) {
            taskCounter++
            val drawable = R.drawable.highlight_selected_square
            addHighlightSelectedSquare(square.position, drawable, highlightSelectedCorrectTag)
        } else {
            val drawable = R.drawable.highlight_square_opponent
            addHighlightSelectedSquare(square.position, drawable, highlightSelectedIncorrectTag)
        }
        currentCoordinate = getNewCoordinate()
        setNewCoordinate(currentCoordinate)
    }

    private fun getNewCoordinate(): String {
        val bitCells = BitCell.entries.toTypedArray()
        val randomCell = bitCells.random()
        return randomCell.name
    }

    private fun setNewCoordinate(coordinate: String) {
        coordinatesToolbarTitle.text = coordinate
    }

    private fun displayCoordinates(){
        chessboardLayout.removeAllViews()
        board.setupInitialBoard()
        initializeBitboardUI()
        setupInitialBoardUI()
        setupSquareListener()
    }

    private fun addHighlightSelectedSquare(position: Long, drawable: Int, tag: String){
        val pos = BitboardManager.positionToRowCol(position)
        val squareFrameLayout = uiSquares[pos.row][pos.col]
        val squareImageView = squareFrameLayout.findViewWithTag<ImageView>("pieceImageView")
        val imageView = ImageView(this)
        imageView.setImageResource(drawable)
        imageView.tag = tag

        if (!showPieces) {
            squareFrameLayout.addView(imageView)
        } else {
            squareFrameLayout.removeView(squareImageView)
            squareFrameLayout.addView(imageView)
            squareFrameLayout.addView(squareImageView)
        }

        val countdownTimer = object : CountDownTimer(1000, 100) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                squareFrameLayout.removeView(imageView)
            }
        }

        countdownTimer.start()
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
                val position = if (playAsWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                uiMapper.addSquare(position, squareLayout)
                squareLayout
            }
        }

        if (!playAsWhite) {
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
                val position = if (playAsWhite) {
                    1L shl ((7 - row) * 8 + col)
                } else {
                    1L shl (row * 8 + (7 - col))
                }
                val square = board.getPiece(position)
                val squareLayout = uiMapper.getSquareView(position)
                if (showPieces) {
                    updateSquareUI(square, squareLayout)
                }
                setupSquareColors(Position(row, col), squareLayout)
                if (showCoordinates) {
                    addRowAndColumnIdentifiers(Position(row, col), squareLayout)
                }
            }
        }
    }

    private fun setupSquareListener() {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = if (playAsWhite) {
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
            val number = if (playAsWhite) (8 - row).toString() else (row + 1).toString()
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
            val letter = if (playAsWhite) ('a' + col).toString() else ('h' - col).toString()
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

    override fun onSaveButtonListener(showCoordinates: Boolean, showPieces: Boolean, playAsWhite: Boolean, minutes: Int, seconds: Int) {
        this.showCoordinates = showCoordinates
        this.showPieces = showPieces
        this.playAsWhite = playAsWhite

        timeLimit = if (seconds < 5 && minutes == 0) {
            5000
        } else {
            (minutes * 60 * 1000 + seconds * 1000).toLong()
        }
        timeLeftInMillis = timeLimit

        updateCountdownText()
        displayCoordinates()
    }

    private fun showSettingsDialog() {
        val dialog = CoordinateSettingsDialogFragment()
        dialog.show(supportFragmentManager, "CoordinateSettingsDialog")
    }

    private fun startCoordinates(){
        val threeText = getString(R.string.three_no_dot)
        val twoText = getString(R.string.two_no_dot)
        val oneText = getString(R.string.one_no_dot)
        val goText = getString(R.string.go)
        currentCoordinate = getNewCoordinate()
        val startCountdownTexts = listOf(threeText, twoText, oneText, goText, currentCoordinate)
        val startCountdownIntervals = 1000L

        for (i in startCountdownTexts.indices) {
            val delay = i * startCountdownIntervals
            Handler(Looper.getMainLooper()).postDelayed({
                coordinatesToolbarTitle.text = startCountdownTexts[i]

                if (i == startCountdownTexts.size - 1) {
                    startTimer()
                }
            }, delay)
        }
    }

    private fun startTimer() {
        isLessonLive = true
        taskCounter = 0
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                lifecycleScope.launch {
                    updateDatabase()
                }
                showResult()
                resetTimer()
            }
        }.start()

        timerRunning = true
    }

    private fun stopTimer() {
        countdownTimer.cancel()
        resetTimer()
    }

    private fun resetTimer() {
        isLessonLive = false
        timerRunning = false
        timeLeftInMillis = timeLimit
        updateCountdownText()
    }

    private fun showResult() {
        coordinatesToolbarTitle.text = getString(R.string.correct_coordinates_found, taskCounter)
    }

    private fun updateCountdownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)
        countdownTimerTextView.text = timeFormatted
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        return when (item.itemId) {
            R.id.stop_coordinates -> {
                if (timerRunning) {
                    stopTimer()
                }
                true
            }

            R.id.play_coordinates -> {
                if (!timerRunning) {
                    startCoordinates()
                }
                true
            }

            else -> false
        }
    }

    private suspend fun updateDatabase() {
        if (userProfile == null) return

        val lessonCompletion = LessonCompletion(
            userID = userProfile!!.userID,
            lessonID = 0,
            type = 4,
            isSolved = true
        )

        userProfileRepository.incrementLessonTaken(userProfile!!.userID)
        lessonCompletionRepository.insertLessonCompletion(lessonCompletion)
    }
}