package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chessmate.R
import com.example.chessmate.util.chess.CastleMove
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.ChessGameManager
import com.example.chessmate.util.chess.ChessGameListener
import com.example.chessmate.util.chess.EnPassantMove
import com.example.chessmate.util.chess.Move
import com.example.chessmate.util.chess.MoveAndCapture
import com.example.chessmate.util.chess.PawnPromotionCaptureMove
import com.example.chessmate.util.chess.PawnPromotionMove
import com.example.chessmate.util.chess.PieceType
import com.example.chessmate.util.chess.PromotionDialogFragment
import com.example.chessmate.util.chess.RegularMove
import com.example.chessmate.util.chess.Square
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.StringBuilder

class ChessGameActivity : AbsThemeActivity(), PromotionDialogFragment.PromotionDialogListener, ChessGameListener {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var chessGameManager: ChessGameManager
    private lateinit var turnNumber: TextView
    private lateinit var whiteLastMove: TextView
    private lateinit var blackLastMove: TextView
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
        chessGameManager = ChessGameManager(this)
        chessboardLayout = findViewById(R.id.chessboard)
        turnNumber = findViewById(R.id.turn_number)
        whiteLastMove = findViewById(R.id.white_last_move)
        blackLastMove = findViewById(R.id.black_last_move)
        val screenWidth = resources.displayMetrics.widthPixels
        squareSize = screenWidth / 8

        //here the the chessboard gets set up with a starting position based on which color starts
        if (startingSide == "white"){
            chessGameManager.initializeGame(true, PieceColor.WHITE, PieceColor.BLACK, depth)
            setupChessboardUI()
            chessGameManager.startGame()
        }else{
            chessGameManager.initializeGame(false, PieceColor.BLACK, PieceColor.WHITE, depth)
            setupChessboardUI()
            chessGameManager.startGame()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun setupChessboardUI(){
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = chessGameManager.chessboard.getSquare(row, col)
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
                    val number = if (chessGameManager.isPlayerStarted) (8 - row).toString() else (row + 1).toString()
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
                    val letter = if (chessGameManager.isPlayerStarted) ('a' + col).toString() else ('h' - col).toString()
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
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.piece_default_pawn_white)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.piece_default_rook_white)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.piece_default_knight_white)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.piece_default_bishop_white)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.piece_default_queen_white)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.piece_default_king_white)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }else{
            when(square.pieceType){
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.piece_default_pawn_black)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.piece_default_rook_black)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.piece_default_knight_black)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.piece_default_bishop_black)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.piece_default_queen_black)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.piece_default_king_black)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }
        return pieceImageView
    }

    private fun handleSquareClick(square: Square) {
        //the println is there for debugging this fucking mess. will get removed eventually
        println("row: ${square.row}, col: ${square.col}, isOccupied: ${square.isOccupied}, PieceType: ${square.pieceType}, PieceColor: ${square.pieceColor}, hasMoved: ${square.hasMoved}, FrameLayout: ${square.frameLayout}, ImageView: ${square.imageView}")

         if (chessGameManager.player.isPlayerTurn && square.pieceColor == chessGameManager.player.playerColor && selectedSquare == null){
             removeHighlightCircles()
             removeHighlightOpponents()
             chessGameManager.handleFirstClick(square)
         } else if (chessGameManager.player.isPlayerTurn && selectedSquare != null && selectedSquare == square){
             removeHighlightCircles()
             removeHighlightOpponents()
             selectedSquare = null
             chessGameManager.clearPlayerMoves()
         }else if(chessGameManager.player.isPlayerTurn && selectedSquare != null && square.pieceColor == chessGameManager.player.playerColor){
             removeHighlightCircles()
             removeHighlightOpponents()
             selectedSquare = null
             chessGameManager.clearPlayerMoves()
             handleSquareClick(square)
         } else if (chessGameManager.player.isPlayerTurn && selectedSquare != null){
             if (selectedSquare!!.row == 1 && square.row == 0 && selectedSquare!!.pieceType == PieceType.PAWN ){
                 if (chessGameManager.isPromotionSquareLegal(selectedSquare!!, square)) {
                     val promotionDialog =
                         PromotionDialogFragment(chessGameManager.isPlayerStarted, this, square)
                     promotionDialog.show(supportFragmentManager, "PromotionDialog")
                 }
             } else {
                 chessGameManager.handleSecondClick(square)
             }
         }
    }

    override fun onMoveMade(move: Move){
        removeHighlightCircles()
        removeHighlightOpponents()
        removeMoveHighlights()

        addMoveHighlights(move.sourceSquare.row, move.sourceSquare.col)
        addMoveHighlights(move.destinationSquare.row, move.destinationSquare.col)

        when (move){
            is RegularMove -> {
                updateRegularMoveUI(move)
            }
            is MoveAndCapture -> {
                updateMoveAndCaptureUI(move)
            }
            is PawnPromotionMove -> {
                updatePawnPromotionMoveUI(move)
            }
            is PawnPromotionCaptureMove -> {
                updatePawnPromotionCaptureMoveUI(move)
            }
            is EnPassantMove -> {
                updateEnPassantMoveUI(move)
            }
            is CastleMove -> {
                updateCastleMoveUI(move)
            }
        }

        chessGameManager.updateMoveTracker(move)
        resetUserData()
    }

    private fun resetUserData(){
        selectedSquare = null
        chessGameManager.clearPlayerMoves()
    }

    private fun updateRegularMoveUI(move: RegularMove) {
        removePieceUI(move.sourceSquare)
        addPieceUI(move.destinationSquare)
    }

    private fun updateMoveAndCaptureUI(move: MoveAndCapture) {
        removePieceUI(move.sourceSquare)
        removePieceUI(move.destinationSquare)
        addPieceUI(move.destinationSquare)
    }

    private fun updatePawnPromotionMoveUI(move: PawnPromotionMove) {
        removePieceUI(move.sourceSquare)
        addPieceUI(move.destinationSquare)

    }

    private fun updatePawnPromotionCaptureMoveUI(move: PawnPromotionCaptureMove) {
        removePieceUI(move.sourceSquare)
        removePieceUI(move.destinationSquare)
        addPieceUI(move.destinationSquare)
    }

    private fun updateEnPassantMoveUI(move: EnPassantMove) {
        //TODO: Not yet implemented
    }

    private fun updateCastleMoveUI(move: CastleMove) {
        removePieceUI(move.sourceSquare)
        addPieceUI(move.destinationSquare)
        removePieceUI(move.rookSourceSquare)
        addPieceUI(move.rookDestinationSquare)
    }

    override fun onPlayerMoveCalculated(legalMoves: MutableList<Move>, square: Square) {
        selectedSquare = square
        for (move in legalMoves){
            when (move){
                is RegularMove -> {
                    if (!move.destinationSquare.isOccupied){
                        addHighlightSquare(move.destinationSquare.row, move.destinationSquare.col)
                    }
                }
                is MoveAndCapture -> {
                    if (move.destinationSquare.isOccupied){
                        addHighlightOpponent(move.destinationSquare.row, move.destinationSquare.col)
                    }
                }
                is PawnPromotionMove -> {
                    if (!move.destinationSquare.isOccupied){
                        addHighlightSquare(move.destinationSquare.row, move.destinationSquare.col)
                    }
                }
                is PawnPromotionCaptureMove -> {
                    if (move.destinationSquare.isOccupied){
                        addHighlightOpponent(move.destinationSquare.row, move.destinationSquare.col)
                    }
                }
                is EnPassantMove -> {}
                is CastleMove -> {
                    addHighlightSquare(move.destinationSquare.row, move.destinationSquare.col)
                }
            }
        }
    }

    override fun kingIsInCheck(square: Square) {
        addHighlightCheck(square.row, square.col)
        chessGameManager.clearPlayerMoves()
    }

    override fun updateMoveTrackerUI() {
        turnNumber.visibility = View.VISIBLE
        whiteLastMove.visibility = View.VISIBLE
        blackLastMove.visibility = View.VISIBLE

        val lastMove = chessGameManager.getLastTrackedMove()
        updateTurnNumberUI()
        when (lastMove){
            is RegularMove -> {
                val sourceSquareNotation = chessGameManager.squareToNotation(lastMove.sourceSquare)
                val destinationSquareNotation = chessGameManager.squareToNotation(lastMove.destinationSquare)

                val resultNotation = StringBuilder()
                resultNotation.append(sourceSquareNotation)
                resultNotation.append("->")
                resultNotation.append(destinationSquareNotation)

                if (lastMove.sourcePieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                    removeWhiteDrawableEndCompat()
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                    removeBlackDrawableEndCompat()
                }

                addDrawableStartCompat(lastMove.sourcePieceType, lastMove.sourcePieceColor)
            }
            is MoveAndCapture -> {
                val sourceSquareNotation = chessGameManager.squareToNotation(lastMove.sourceSquare)
                val destinationSquareNotation = chessGameManager.squareToNotation(lastMove.destinationSquare)

                val resultNotation = StringBuilder()
                resultNotation.append(sourceSquareNotation)
                resultNotation.append("x")
                resultNotation.append(destinationSquareNotation)

                if (lastMove.sourcePieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                    removeWhiteDrawableEndCompat()
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                    removeBlackDrawableEndCompat()
                }

                addDrawableStartCompat(lastMove.sourcePieceType, lastMove.sourcePieceColor)
            }
            is PawnPromotionMove -> {
                val sourceSquareNotation = chessGameManager.squareToNotation(lastMove.sourceSquare)
                val destinationSquareNotation = chessGameManager.squareToNotation(lastMove.destinationSquare)

                val resultNotation = StringBuilder()
                resultNotation.append(sourceSquareNotation)
                resultNotation.append("->")
                resultNotation.append(destinationSquareNotation)
                resultNotation.append("=")

                if (lastMove.promotedPieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                }

                addDrawableStartCompat(PieceType.PAWN, lastMove.promotedPieceColor)
                addDrawableEndCompat(lastMove.promotedPieceType, lastMove.promotedPieceColor)
            }
            is PawnPromotionCaptureMove -> {
                val sourceSquareNotation = chessGameManager.squareToNotation(lastMove.sourceSquare)
                val destinationSquareNotation = chessGameManager.squareToNotation(lastMove.destinationSquare)

                val resultNotation = StringBuilder()
                resultNotation.append(sourceSquareNotation)
                resultNotation.append("x")
                resultNotation.append(destinationSquareNotation)
                resultNotation.append("=")

                if (lastMove.promotedPieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                }

                addDrawableStartCompat(PieceType.PAWN, lastMove.promotedPieceColor)
                addDrawableEndCompat(lastMove.promotedPieceType, lastMove.promotedPieceColor)
            }
            is EnPassantMove -> {
                val sourceSquareNotation = chessGameManager.squareToNotation(lastMove.sourceSquare)
                val destinationSquareNotation = chessGameManager.squareToNotation(lastMove.destinationSquare)

                val resultNotation = StringBuilder()
                resultNotation.append(sourceSquareNotation)
                resultNotation.append("x")
                resultNotation.append(destinationSquareNotation)

                if (lastMove.sourcePieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                    removeWhiteDrawableEndCompat()
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                    removeBlackDrawableEndCompat()
                }

                addDrawableStartCompat(PieceType.PAWN, lastMove.sourcePieceColor)
            }
            is CastleMove -> {
                val resultNotation = StringBuilder()

                if (lastMove.isKingSideCastles){
                    resultNotation.append("O-O")
                } else {
                    resultNotation.append("O-O-O")
                }

                if (lastMove.sourcePieceColor == PieceColor.WHITE){
                    updateWhiteLastMoveUI(resultNotation.toString())
                    removeWhiteDrawableEndCompat()
                } else {
                    updateBlackLastMoveUI(resultNotation.toString())
                    removeBlackDrawableEndCompat()
                }

                addDrawableStartCompat(PieceType.KING, lastMove.sourcePieceColor)
            }
        }
    }

    override fun checkForStalemate() {
        //TODO: Not yet implemented
    }

    override fun checkForCheckmate() {
        //TODO: Not yet implemented
    }

    private fun updateTurnNumberUI(){
        val turnStr = chessGameManager.turnNumber.toString() + "."
        turnNumber.text = turnStr
    }

    private fun updateWhiteLastMoveUI(notation: String){
        whiteLastMove.text = notation
    }

    private fun updateBlackLastMoveUI(notation: String){
        blackLastMove.text = notation
    }

    private fun addDrawableStartCompat(pieceType: PieceType, pieceColor: PieceColor){
       when (pieceType){
           PieceType.PAWN -> {
               if (pieceColor == PieceColor.WHITE){
                   val pawnDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_pawn_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(pawnDrawable, null, null, null)
               } else {
                   val pawnDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_pawn_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(pawnDrawable, null, null, null)
               }
           }
           PieceType.ROOK -> {
               if (pieceColor == PieceColor.WHITE){
                   val rookDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_rook_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(rookDrawable, null, null, null)
               } else {
                   val rookDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_rook_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(rookDrawable, null, null, null)
               }
           }
           PieceType.KNIGHT -> {
               if (pieceColor == PieceColor.WHITE){
                   val knightDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_knight_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(knightDrawable, null, null, null)
               } else {
                   val knightDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_knight_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(knightDrawable, null, null, null)
               }
           }
           PieceType.BISHOP -> {
               if (pieceColor == PieceColor.WHITE){
                   val bishopDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_bishop_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(bishopDrawable, null, null, null)
               } else {
                   val bishopDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_bishop_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(bishopDrawable, null, null, null)
               }
           }
           PieceType.QUEEN -> {
               if (pieceColor == PieceColor.WHITE){
                   val queenDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_queen_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(queenDrawable, null, null, null)
               } else {
                   val queenDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_queen_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(queenDrawable, null, null, null)
               }
           }
           PieceType.KING -> {
               if (pieceColor == PieceColor.WHITE){
                   val kingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_king_white_24)
                   whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(kingDrawable, null, null, null)
               } else {
                   val kingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_king_black_24)
                   blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(kingDrawable, null, null, null)
               }
           }
       }
    }

    private fun addDrawableEndCompat(pieceType: PieceType, pieceColor: PieceColor){
        when (pieceType){
            PieceType.PAWN -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val pawnDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_pawn_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, pawnDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val pawnDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_pawn_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, pawnDrawable, null)
                }
            }
            PieceType.ROOK -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val rookDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_rook_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, rookDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val rookDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_rook_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, rookDrawable, null)
                }
            }
            PieceType.KNIGHT -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val knightDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_knight_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, knightDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val knightDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_knight_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, knightDrawable, null)
                }
            }
            PieceType.BISHOP -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val bishopDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_bishop_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, bishopDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val bishopDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_bishop_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, bishopDrawable, null)
                }
            }
            PieceType.QUEEN -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val queenDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_queen_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, queenDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val queenDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_queen_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, queenDrawable, null)
                }
            }
            PieceType.KING -> {
                if (pieceColor == PieceColor.WHITE){
                    val existingStartDrawable = whiteLastMove.compoundDrawables
                    val kingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_king_white_24)
                    whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, kingDrawable, null)
                } else {
                    val existingStartDrawable = blackLastMove.compoundDrawables
                    val kingDrawable = ContextCompat.getDrawable(this, R.drawable.ic_default_king_black_24)
                    blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, kingDrawable, null)
                }
            }
        }
    }

    private fun removeWhiteDrawableEndCompat(){
        val existingStartDrawable = whiteLastMove.compoundDrawables
        whiteLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, null, null)
    }

    private fun removeBlackDrawableEndCompat(){
        val existingStartDrawable = blackLastMove.compoundDrawables
        blackLastMove.setCompoundDrawablesRelativeWithIntrinsicBounds(existingStartDrawable[0], null, null, null)
    }

    private fun addPieceUI(square: Square) {
        val pieceImageView = createPieceImageView(square)
        square.imageView = pieceImageView
        square.frameLayout?.addView(pieceImageView)
    }

    private fun removePieceUI(square: Square) {
        square.frameLayout?.removeView(square.imageView)
        square.clearUI()
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.nav_resign -> {
                println("TrackedMoves:")
                for (trackedMove in chessGameManager.moveTracker){
                    println("TrackedMove: $trackedMove")
                }
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
        val frameLayout = FrameLayout(this)
        val squareSize = chessboardLayout.width / 8
        val circleSize = squareSize / 3

        val params = GridLayout.LayoutParams().apply {
            width = squareSize
            height = squareSize
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(col)
        }

        val circleParams = FrameLayout.LayoutParams(circleSize, circleSize).apply {
            gravity = Gravity.CENTER
        }

        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_circle)

        frameLayout.addView(imageView, circleParams)
        frameLayout.tag = highlightCircleTag

        chessboardLayout.addView(frameLayout, params)
    }

    private fun addHighlightOpponent(row: Int, col: Int) {
        val square = chessGameManager.chessboard.getSquare(row, col)
        if (square.pieceType == PieceType.KING) return
        val squareFrameLayout = square.frameLayout
        val squareImageView = square.imageView
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag
        squareFrameLayout?.removeView(squareImageView)
        squareFrameLayout?.addView(imageView)
        squareFrameLayout?.addView(squareImageView)
    }

    private fun addMoveHighlights(row: Int, col: Int){
        val square = chessGameManager.chessboard.getSquare(row, col)
        val squareFrameLayout = square.frameLayout
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout?.addView(imageView)
    }

    private fun addHighlightCheck(row: Int, col: Int){
        val square = chessGameManager.chessboard.getSquare(row, col)
        val squareFrameLayout = square.frameLayout
        val squareImageView = square.imageView
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_opponent)
        imageView.tag = highlightOpponentTag

        squareFrameLayout?.removeView(squareImageView)
        squareFrameLayout?.addView(imageView)
        squareFrameLayout?.addView(squareImageView)

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
                squareFrameLayout?.removeView(imageView)
            }
        }

        countdownTimer.start()
    }

    private fun removeMoveHighlights(){
        val moveHighlightsToRemove = mutableListOf<View>()

        for (i in 0 until chessboardLayout.childCount) {
            val squareFrameLayout = chessboardLayout.getChildAt(i) as? FrameLayout
            if (squareFrameLayout != null) {
                for (j in 0 until squareFrameLayout.childCount) {
                    val view = squareFrameLayout.getChildAt(j)
                    if (view.tag == highlightMoveTag) {
                        moveHighlightsToRemove.add(view)
                    }
                }
            }
        }

        moveHighlightsToRemove.forEach { highlight ->
            val parentFrameLayout = highlight.parent as? FrameLayout
            parentFrameLayout?.removeView(highlight)
        }
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
            val squareFrameLayout = chessboardLayout.getChildAt(i) as? FrameLayout
            if (squareFrameLayout != null) {
                for (j in 0 until squareFrameLayout.childCount) {
                    val view = squareFrameLayout.getChildAt(j)
                    if (view.tag == highlightOpponentTag) {
                        opponentHighlightsToRemove.add(view)
                    }
                }
            }
        }

        opponentHighlightsToRemove.forEach { highlight ->
            val parentFrameLayout = highlight.parent as? FrameLayout
            parentFrameLayout?.removeView(highlight)
        }
    }

    override fun onPieceSelected(pieceType: PieceType, destinationSquare: Square) {
        when(pieceType){
            PieceType.QUEEN -> {
                chessGameManager.handleSecondClick(destinationSquare, pieceType)
            }
            PieceType.ROOK -> {
                chessGameManager.handleSecondClick(destinationSquare, pieceType)
            }
            PieceType.BISHOP -> {
                chessGameManager.handleSecondClick(destinationSquare, pieceType)
            }
            PieceType.KNIGHT -> {
                chessGameManager.handleSecondClick(destinationSquare, pieceType)
            }
            else -> throw IllegalArgumentException("Unexpected PieceType: $pieceType")
        }
    }
}