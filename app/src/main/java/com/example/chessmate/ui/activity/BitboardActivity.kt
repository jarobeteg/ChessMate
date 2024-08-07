package com.example.chessmate.ui.activity

import android.content.Context
import android.content.Intent
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
import com.example.chessmate.util.chess.EndGameDialogFragment
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.PromotionDialogFragment
import com.example.chessmate.util.chess.bitboard.BitCell
import com.example.chessmate.util.chess.bitboard.BitMove
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardListener
import com.example.chessmate.util.chess.bitboard.BitboardManager
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper
import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.PieceType
import com.google.android.material.bottomnavigation.BottomNavigationView

class BitboardActivity : AbsThemeActivity(), BitboardListener, PromotionDialogFragment.PromotionDialogListener, EndGameDialogFragment.OnHomeButtonClickListener {
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
    private val highlightSelectedTag = "highlight_selected_square"
    private var selectedSquare: BitSquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bitboard_bottom_navigation)

        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        var startingSide = sharedPreferences.getString("starting_side", "random")
        GameContext.depth = 4 + sharedPreferences.getInt("depth", 0)
        GameContext.topMoveSearch = GameContext.depth - 1
        if (startingSide == "random") {
            val random = java.util.Random()
            val isWhite = random.nextBoolean()
            startingSide = if (isWhite) "white" else "black"
        }

        isPlayerStarted = startingSide == "white"

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

    override fun onHomeButtonClicked() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val dialog = supportFragmentManager.findFragmentByTag("EndGameDialog")
        if (dialog != null && dialog.isVisible) {

        } else {
            super.onBackPressed()
        }
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
                    handleSquareClick(gameManager.getBitSquare(position))
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
        when {
            isFirstSelection(square) -> handleFirstSelection(square)
            isDeselecting(square) -> handleDeselect()
            isNewSelection(square) -> handleNewSelection(square)
            isSecondClick() -> handleSecondClick(square)
            else -> {}
        }
    }

    private fun isFirstSelection(square: BitSquare): Boolean {
        return GameContext.isPlayerTurn && square.color == GameContext.playerColor && selectedSquare == null
    }

    private fun handleFirstSelection(square: BitSquare) {
        removeHighlightsFromSquares()
        gameManager.processFirstClick(square)
    }

    private fun isDeselecting(square: BitSquare): Boolean {
        return GameContext.isPlayerTurn && selectedSquare != null && selectedSquare == square
    }

    private fun handleDeselect() {
        removeHighlightsFromSquares()
        selectedSquare = null
    }

    private fun isNewSelection(square: BitSquare): Boolean {
        return GameContext.isPlayerTurn && square.color == GameContext.playerColor && selectedSquare != null
    }

    private fun handleNewSelection(square: BitSquare) {
        removeHighlightsFromSquares()
        gameManager.processFirstClick(square)
    }

    private fun isSecondClick(): Boolean {
        return GameContext.isPlayerTurn && selectedSquare != null
    }

    private fun handleSecondClick(square: BitSquare) {
        gameManager.processSecondClick(square)
    }

    override fun onPlayerMoveCalculated(moves: MutableList<BitMove>, square: BitSquare) {
        if (moves.isEmpty()) {
            if (isPlayerInCheck()) {
                addHighlightCheck(gameManager.getPlayerKingPosition())
            }
            selectedSquare = null
            return
        }
        removeHighlightsFromSquares()
        for (move in moves) {
            if (move.capturedPiece != BitPiece.NONE) {
                addHighlightOpponent(move.to, move.capturedPiece)
            } else {
                addHighlightSquare(move.to)
            }
        }
        selectedSquare = square
        addHighlightSelectedSquare(square.position)
    }

    private fun isPlayerInCheck(): Boolean {
        return gameManager.isKingInCheck(false)
    }

    private fun isBotInCheck(): Boolean {
        return gameManager.isKingInCheck(true)
    }

    override fun onPlayerMoveMade(bitboard: Bitboard, move: BitMove) {
        removeHighlightMoves()
        removeHighlightsFromSquares()
        selectedSquare = null
        addHighlightMove(move.from)
        addHighlightMove(move.to)
        updateBitboardStateUI(bitboard)
        updateMoveTrackerToolbar(isBotInCheck())
        gameManager.switchTurns()
    }

    override fun onBotMoveMade(bitboard: Bitboard, move: BitMove) {
        removeHighlightMoves()
        addHighlightMove(move.from)
        addHighlightMove(move.to)
        updateBitboardStateUI(bitboard)
        updateMoveTrackerToolbar(isPlayerInCheck())
        gameManager.switchTurns()
    }

    override fun showEndGameDialog(endGameResult: String) {
        val endGameDialog = EndGameDialogFragment(endGameResult)
        endGameDialog.show(supportFragmentManager, "EndGameDialog")
    }

    private fun updateMoveTrackerToolbar(wasCheckGiven: Boolean) {
        turnNumber.visibility = View.VISIBLE
        val text = "${gameManager.turnNumber}."
        turnNumber.text = text
        val lastTrackedMove = gameManager.getLastTrackedMove()
        if (lastTrackedMove.isMoveMadeByWhite) {
            updateWhiteLastMove(wasCheckGiven)
        } else {
            updateBlackLastMove(wasCheckGiven)
        }
    }

    private fun updateWhiteLastMove(wasCheckGiven: Boolean) {
        whiteLastMove.visibility = View.VISIBLE
        val lastTrackedMove = gameManager.getLastTrackedWhiteMove()
        var moveNotation = getWhiteMoveNotation(lastTrackedMove.bitMove)
        if (wasCheckGiven) moveNotation += "+"
        whiteLastMove.text = moveNotation
    }

    private fun getWhiteMoveNotation(move: BitMove): String {
        return when {
            move.isCastling -> getCastlingNotation(move.to)
            move.piece == BitPiece.WHITE_PAWN -> getPawnNotation(move)
            move.piece in listOf(BitPiece.WHITE_KNIGHT, BitPiece.WHITE_BISHOP, BitPiece.WHITE_ROOK, BitPiece.WHITE_QUEEN, BitPiece.WHITE_KING) -> getPieceNotation(move)
            else -> "---"
        }
    }

    private fun updateBlackLastMove(wasCheckGiven: Boolean) {
        blackLastMove.visibility = View.VISIBLE
        val lastTrackedMove = gameManager.getLastTrackedBlackMove()
        var moveNotation = getBlackMoveNotation(lastTrackedMove.bitMove)
        if (wasCheckGiven) moveNotation += "+"
        blackLastMove.text = moveNotation
    }

    private fun getBlackMoveNotation(move: BitMove): String {
        return when {
            move.isCastling -> getCastlingNotation(move.to)
            move.piece == BitPiece.BLACK_PAWN -> getPawnNotation(move)
            move.piece in listOf(BitPiece.BLACK_KNIGHT, BitPiece.BLACK_BISHOP, BitPiece.BLACK_ROOK, BitPiece.BLACK_QUEEN, BitPiece.BLACK_KING) -> getPieceNotation(move)
            else -> "---"
        }
    }

    private fun getPieceNotation(move: BitMove): String {
        val pieceNotation = when (move.piece) {
            BitPiece.WHITE_KNIGHT, BitPiece.BLACK_KNIGHT -> "N"
            BitPiece.WHITE_BISHOP, BitPiece.BLACK_BISHOP -> "B"
            BitPiece.WHITE_ROOK, BitPiece.BLACK_ROOK -> "R"
            BitPiece.WHITE_QUEEN, BitPiece.BLACK_QUEEN -> "Q"
            BitPiece.WHITE_KING, BitPiece.BLACK_KING -> "K"
            else -> ""
        }
        val toSquare = gameManager.getSquareNotation(move.to)
        return if (move.capturedPiece != BitPiece.NONE) {
            "$pieceNotation${gameManager.getFileName(move.from)}x$toSquare"
        } else {
            "$pieceNotation${gameManager.getFileName(move.from)}$toSquare"
        }
    }

    private fun getPawnNotation(move: BitMove): String {
        val toSquare = gameManager.getSquareNotation(move.to)
        return if (move.promotion != BitPiece.NONE) {
            "${gameManager.getSquareNotation(move.to)}=${getPromotionNotation(move.promotion)}"
        } else if (move.capturedPiece != BitPiece.NONE) {
            "${gameManager.getFileName(move.from)}x$toSquare"
        } else {
            toSquare
        }
    }

    private fun getCastlingNotation(to: Long): String {
        return when (to) {
            BitCell.G1.bit, BitCell.G8.bit -> "O-O"
            BitCell.C1.bit, BitCell.C8.bit -> "O-O-O"
            else -> "---"
        }
    }

    private fun getPromotionNotation(promotion: BitPiece): String {
        return when (promotion) {
            BitPiece.WHITE_KNIGHT, BitPiece.BLACK_KNIGHT -> "N"
            BitPiece.WHITE_BISHOP, BitPiece.BLACK_BISHOP -> "B"
            BitPiece.WHITE_ROOK, BitPiece.BLACK_ROOK -> "R"
            BitPiece.WHITE_QUEEN, BitPiece.BLACK_QUEEN -> "Q"
            else -> "---"
        }
    }

    private fun updateBitboardStateUI(bitboard: Bitboard) {
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

    private fun addHighlightSelectedSquare(position: Long) {
        val pos = gameManager.positionToRowCol(position)
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

    private fun addHighlightCheck(position: Long){
        val pos = gameManager.positionToRowCol(position)
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

    override fun showPromotionDialog(square: BitSquare) {
        val dialog = PromotionDialogFragment(isPlayerStarted, this, selectedSquare!!, square)
        dialog.show(supportFragmentManager, PromotionDialogFragment.TAG)
    }

    override fun onPieceSelected(pieceType: PieceType, fromSquare: BitSquare, toSquare: BitSquare) {
        gameManager.processPawnPromotion(pieceType, fromSquare, toSquare)
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.nav_resign -> {
                gameManager.resign()
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