package com.example.chessmate.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.chessmate.R
import com.example.chessmate.util.chess.Bishop
import com.example.chessmate.util.chess.PieceColor
import com.example.chessmate.util.chess.Chessboard
import com.example.chessmate.util.chess.King
import com.example.chessmate.util.chess.Knight
import com.example.chessmate.util.chess.MoveTracker
import com.example.chessmate.util.chess.Pawn
import com.example.chessmate.util.chess.PieceType
import com.example.chessmate.util.chess.PromotionDialogFragment
import com.example.chessmate.util.chess.Queen
import com.example.chessmate.util.chess.Rook
import com.example.chessmate.util.chess.Square

class GameChess : AbsThemeActivity(), PromotionDialogFragment.PromotionDialogListener {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var chessboard: Chessboard
    private var moveTracker: MutableList<MoveTracker> = mutableListOf()
    private var turnNumber: Int = 1
    private var isWhiteStarting: Boolean = false
    private var squareSize: Int = 0
    private val highlightCircleTag = "highlight_circle"
    private val highlightOpponentTag = "highlight_opponent"
    private val highlightMoveTag = "highlight_move"
    private var selectedSquare: Square? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_game)

        //this code snippet determines the user starting side
        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        var startingSide = sharedPreferences.getString("starting_side", "random")
        if (startingSide == "random") {
            val random = java.util.Random()
            val isWhite = random.nextBoolean()
            startingSide = if (isWhite) "white" else "black"
        }

        //the chessboard gets initialized with empty squares and square sizes are determined based on the device's display metrics
        chessboard = Chessboard()
        chessboardLayout = findViewById(R.id.chessboard)
        val screenWidth = resources.displayMetrics.widthPixels
        squareSize = screenWidth / 8

        //here the the chessboard gets set up with a starting position based on which color starts
        if (startingSide == "white"){
            isWhiteStarting = true
            initializeStartingPosition()
            setupChessboard()
        }else{
            isWhiteStarting = false
            initializeStartingPosition()
            setupChessboard()
        }
    }

    /** before this is called an initial starting position of pieces has been set
     * here we iterate through the chessboard and we get each square on the chessboard and we create a frame layout that represents the square UI
     * the square sizes have been determined previously and these frame layouts will have this size
     * then colors of the squares are determined and set
     * after that we check if a piece have been placed on the square during the initialization of the starting position
     * if isOccupied returns true wew create a piece image view and add it to the square frame layout and also add it to the square class (we need this later for piece movement)
     * after this the numbers and letters are determined and placed on the first col and bottom row
     * each square gets a click listener. well the square frame layout gets a click listener to be more precise
     * then at last the frame layout is saved in the square class (for later we need this to show move highlights etc)
     * and the square frame layout is added to the chessboard layout which is a grid layout
     * */
    private fun setupChessboard(){
        val lightSquareColor = R.color.default_light_square_color
        val darkSquareColor = R.color.default_dark_square_color

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val square = chessboard.getSquare(row, col)
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
                    val number = if (isWhiteStarting) (8 - row).toString() else (row + 1).toString()
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
                    val letter = if (isWhiteStarting) ('a' + col).toString() else ('h' - col).toString()
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

    //first this is called to place pieces on the chessboard. this just places the piece types only and not the images of pieces
    //the chessboard have been previously initialized to the chessboard.placePiece places a piece type with color to a square with row and col to setup the starting position
    //once this is done the setupChessboard is called and does the UI part for the chessboard
    private fun initializeStartingPosition() {
        if (isWhiteStarting){//user starts as white
            chessboard.placePiece(6, 0, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 1, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 2, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 3, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 4, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 5, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 6, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(6, 7, PieceColor.WHITE, PieceType.PAWN)

            chessboard.placePiece(7, 0, PieceColor.WHITE, PieceType.ROOK)
            chessboard.placePiece(7, 1, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(7, 2, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(7, 3, PieceColor.WHITE, PieceType.QUEEN)
            chessboard.placePiece(7, 4, PieceColor.WHITE, PieceType.KING)
            chessboard.placePiece(7, 5, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(7, 6, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(7, 7, PieceColor.WHITE, PieceType.ROOK)

            chessboard.placePiece(1, 0, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 1, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 2, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 3, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 4, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 5, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 6, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(1, 7, PieceColor.BLACK, PieceType.PAWN)

            chessboard.placePiece(0, 0, PieceColor.BLACK, PieceType.ROOK)
            chessboard.placePiece(0, 1, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(0, 2, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(4, 7, PieceColor.BLACK, PieceType.QUEEN)
            chessboard.placePiece(0, 4, PieceColor.BLACK, PieceType.KING)
            chessboard.placePiece(0, 5, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(0, 6, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(0, 7, PieceColor.BLACK, PieceType.ROOK)
        }else{
            chessboard.placePiece(1, 0, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 1, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 2, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 3, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 4, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 5, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 6, PieceColor.WHITE, PieceType.PAWN)
            chessboard.placePiece(1, 7, PieceColor.WHITE, PieceType.PAWN)

            chessboard.placePiece(0, 0, PieceColor.WHITE, PieceType.ROOK)
            chessboard.placePiece(0, 1, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(0, 2, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(0, 3, PieceColor.WHITE, PieceType.KING)
            chessboard.placePiece(0, 4, PieceColor.WHITE, PieceType.QUEEN)
            chessboard.placePiece(0, 5, PieceColor.WHITE, PieceType.BISHOP)
            chessboard.placePiece(0, 6, PieceColor.WHITE, PieceType.KNIGHT)
            chessboard.placePiece(0, 7, PieceColor.WHITE, PieceType.ROOK)

            chessboard.placePiece(6, 0, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 1, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 2, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 3, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 4, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 5, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 6, PieceColor.BLACK, PieceType.PAWN)
            chessboard.placePiece(6, 7, PieceColor.BLACK, PieceType.PAWN)

            chessboard.placePiece(7, 0, PieceColor.BLACK, PieceType.ROOK)
            chessboard.placePiece(7, 1, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(7, 2, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(7, 3, PieceColor.BLACK, PieceType.KING)
            chessboard.placePiece(7, 4, PieceColor.BLACK, PieceType.QUEEN)
            chessboard.placePiece(7, 5, PieceColor.BLACK, PieceType.BISHOP)
            chessboard.placePiece(7, 6, PieceColor.BLACK, PieceType.KNIGHT)
            chessboard.placePiece(7, 7, PieceColor.BLACK, PieceType.ROOK)
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
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.default_pawn_white)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.default_rook_white)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.default_knight_white)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.default_bishop_white)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.default_queen_white)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.default_king_white)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }else{
            when(square.pieceType){
                PieceType.PAWN -> pieceImageView.setImageResource(R.drawable.default_pawn_black)
                PieceType.ROOK -> pieceImageView.setImageResource(R.drawable.default_rook_black)
                PieceType.KNIGHT -> pieceImageView.setImageResource(R.drawable.default_knight_black)
                PieceType.BISHOP -> pieceImageView.setImageResource(R.drawable.default_bishop_black)
                PieceType.QUEEN -> pieceImageView.setImageResource(R.drawable.default_queen_black)
                PieceType.KING -> pieceImageView.setImageResource(R.drawable.default_king_black)
                else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
            }
        }
        return pieceImageView
    }

    /**
     * this handles the square clicks and this probably can be prettier or more optimized. idk might refractor the code later
     * there is selected square variable that is set to null. this is how the app knows if the user clicks for the first time or not meaning if a square has been selected that means the user has clicked on a square
     * so on the first click highlights are removed and we check which square has been clicked
     * for example if a pawn is clicked a Pawn is created and show all the available moves for that pawn.
     * the second time we click we first check if the same square was clicked if yes we remove all highlights and set the selected square to null
     * we also check if we clicked on a piece that has the same color as the one on the selected square meaning we clicked on another of our square
     * we remove all the highlights and set the selected square to null again but now we call the handle square click again with the square we just clicked
     * and it would show immediately the available moves of that piece otherwise if this is not implemented then we would have to click twice to choose a different piece
     * on the second click which would mean that we either move or take a piece (here the pawn is an exception because it can promote so with second click we check if the destination square is the promotion square)
     */
    private fun handleSquareClick(square: Square) {
        //the println is there for debugging this fucking mess. will get removed eventually
        println("row: ${square.row}, col: ${square.col}, isOccupied: ${square.isOccupied}, PieceType: ${square.pieceType}, PieceColor: ${square.pieceColor}, FrameLayout: ${square.frameLayout}, ImageView: ${square.imageView}")

        when{
            isWhiteStarting && square.pieceColor == PieceColor.WHITE && selectedSquare == null -> { //first click as white
                val kingSquare = chessboard.getWhiteKingSquare()
                removeHighlightCircles()
                removeHighlightOpponents()
                when(square.pieceType){
                    PieceType.PAWN -> {
                        val pawn = Pawn(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.WHITE)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.WHITE)){
                                square.pieceType = PieceType.PAWN
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.PAWN
                                square.isOccupied = true
                                pawn.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.ROOK -> {
                        val rook = Rook(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.WHITE)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.WHITE)){
                                square.pieceType = PieceType.ROOK
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.ROOK
                                square.isOccupied = true
                                rook.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.KNIGHT -> {
                        val knight = Knight(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.WHITE)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.WHITE)){
                                square.pieceType = PieceType.KNIGHT
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.KNIGHT
                                square.isOccupied = true
                                knight.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.BISHOP -> {
                        val bishop = Bishop(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.WHITE)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.WHITE)){
                                square.pieceType = PieceType.BISHOP
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.BISHOP
                                square.isOccupied = true
                                bishop.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.QUEEN -> {
                        val queen = Queen(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.WHITE)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.WHITE)){
                                square.pieceType = PieceType.QUEEN
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.QUEEN
                                square.isOccupied = true
                                queen.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.KING -> {
                        val king = King(this, chessboardLayout, chessboard, square)
                        king.showHighlightSquares()
                        selectedSquare = square
                    }
                    else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                }
            }

            isWhiteStarting && selectedSquare != null -> { //second click as white
                if (selectedSquare == square){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                } else if (selectedSquare?.pieceColor == square.pieceColor){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                    handleSquareClick(square)
                } else {
                    val destinationSquare = square
                    when(selectedSquare!!.pieceType){
                        PieceType.PAWN -> {
                            val pawn = Pawn(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (pawn.isValidMove(destinationSquare)){
                                if (destinationSquare.row == 0){
                                    val promotionDialog = PromotionDialogFragment(isWhiteStarting, this, selectedSquare!!, destinationSquare)
                                    promotionDialog.show(supportFragmentManager, "PromotionDialog")
                                }else {
                                    movePiece(selectedSquare!!, destinationSquare)
                                }
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.ROOK -> {
                            val rook = Rook(this, chessboardLayout, chessboard, selectedSquare!!)
                                if (rook.isValidMove(destinationSquare)){
                                    movePiece(selectedSquare!!, destinationSquare)
                                }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.BISHOP -> {
                            val bishop = Bishop(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (bishop.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.KNIGHT -> {
                            val knight = Knight(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (knight.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.QUEEN -> {
                            val queen = Queen(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (queen.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.KING -> {
                            val king = King(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (king.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                    }
                }
            }

            !isWhiteStarting && square.pieceColor == PieceColor.BLACK && selectedSquare == null -> { //first click as black
                val kingSquare = chessboard.getBlackKingSquare()
                removeHighlightCircles()
                removeHighlightOpponents()
                when(square.pieceType){
                    PieceType.PAWN -> {
                        val pawn = Pawn(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.BLACK)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.BLACK)){
                                square.pieceType = PieceType.PAWN
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.PAWN
                                square.isOccupied = true
                                pawn.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.ROOK -> {
                        val rook = Rook(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.BLACK)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.BLACK)){
                                square.pieceType = PieceType.ROOK
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.ROOK
                                square.isOccupied = true
                                rook.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.KNIGHT -> {
                        val knight = Knight(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.BLACK)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.BLACK)){
                                square.pieceType = PieceType.KNIGHT
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.KNIGHT
                                square.isOccupied = true
                                knight.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.BISHOP -> {
                        val bishop = Bishop(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.BLACK)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.BLACK)){
                                square.pieceType = PieceType.BISHOP
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.BISHOP
                                square.isOccupied = true
                                bishop.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.QUEEN -> {
                        val queen = Queen(this, chessboardLayout, chessboard, square)
                        if (!chessboard.isKingInCheck(chessboard, kingSquare!!, PieceColor.BLACK)) {
                            square.pieceType = null
                            square.isOccupied = false
                            if (chessboard.isKingInCheck(chessboard, kingSquare, PieceColor.BLACK)){
                                square.pieceType = PieceType.QUEEN
                                square.isOccupied = true
                            }else {
                                square.pieceType = PieceType.QUEEN
                                square.isOccupied = true
                                queen.showHighlightSquares()
                                selectedSquare = square
                            }
                        }else{
                            addHighlightCheck(kingSquare.row, kingSquare.col)
                        }
                    }
                    PieceType.KING -> {
                        val king = King(this, chessboardLayout, chessboard, square)
                        king.showHighlightSquares()
                        selectedSquare = square
                    }
                    else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                }
            }

            !isWhiteStarting && selectedSquare != null -> { //second click as black
                if (selectedSquare == square){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                } else if (selectedSquare?.pieceColor == square.pieceColor){
                    removeHighlightCircles()
                    removeHighlightOpponents()
                    selectedSquare = null
                    handleSquareClick(square)
                } else {
                    val destinationSquare = square
                    when(selectedSquare!!.pieceType){
                        PieceType.PAWN -> {
                            val pawn = Pawn(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (pawn.isValidMove(destinationSquare)){
                                if (destinationSquare.row == 0){
                                    val promotionDialog = PromotionDialogFragment(isWhiteStarting, this, selectedSquare!!, destinationSquare)
                                    promotionDialog.show(supportFragmentManager, "PromotionDialog")
                                }else {
                                    movePiece(selectedSquare!!, destinationSquare)
                                }
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.ROOK -> {
                            val rook = Rook(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (rook.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.BISHOP -> {
                            val bishop = Bishop(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (bishop.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.KNIGHT -> {
                            val knight = Knight(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (knight.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.QUEEN -> {
                            val queen = Queen(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (queen.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        PieceType.KING -> {
                            val king = King(this, chessboardLayout, chessboard, selectedSquare!!)
                            if (king.isValidMove(destinationSquare)){
                                movePiece(selectedSquare!!, destinationSquare)
                            }
                            removeHighlightCircles()
                            removeHighlightOpponents()
                            selectedSquare = null
                        }

                        else -> throw IllegalArgumentException("Unexpected PieceType: ${square.pieceType}")
                    }
                }
            }
        }
    }

    //track each move in a chess game
    private fun trackMove(move: MoveTracker){
        moveTracker.add(move)
    }

    //if the piece just moves then we give the destination square the piece type and color and we set to occupied and also we add the move highlight so the user can we what was the last move they did
    //the source square gets cleared and the destination square gets an image view based on which piece type is on the square
    //if the destination square is occupied meaning the user choose to take that piece we first have to clear that square before moving our piece onto that square.
    private fun movePiece(sourceSquare: Square, destinationSquare: Square) {
        removeMoveHighlights()
        if (destinationSquare.isOccupied){
            removePiece(destinationSquare)
        }
        destinationSquare.pieceType = sourceSquare.pieceType
        destinationSquare.isOccupied = true
        destinationSquare.pieceColor = sourceSquare.pieceColor
        addMoveHighlights(sourceSquare.row, sourceSquare.col)
        addMoveHighlights(destinationSquare.row, destinationSquare.col)
        removePiece(sourceSquare)
        addPiece(destinationSquare)
        val isWhiteToMove = destinationSquare.pieceColor == PieceColor.BLACK
        val move = MoveTracker(sourceSquare, destinationSquare, turnNumber, isWhiteToMove)
        turnNumber++
        trackMove(move)
    }

    private fun addPiece(square: Square) {
        val pieceImageView = createPieceImageView(square)
        square.imageView = pieceImageView
        square.frameLayout?.addView(pieceImageView)
    }

    private fun removePiece(square: Square) {
        square.frameLayout?.removeView(square.imageView)
        square.clearSquare()
    }

    private fun addMoveHighlights(row: Int, col: Int){
        val square = chessboard.getSquare(row, col)
        val squareFrameLayout = square.frameLayout
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.highlight_square_move)
        imageView.tag = highlightMoveTag
        squareFrameLayout?.addView(imageView)
    }

    private fun addHighlightCheck(row: Int, col: Int){
        val square = chessboard.getSquare(row, col)
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

    override fun onPieceSelected(pieceType: PieceType, sourceSquare: Square, destinationSquare: Square) {
        when(pieceType){
            PieceType.QUEEN -> {
                sourceSquare.pieceType = pieceType
                movePiece(sourceSquare, destinationSquare)
            }
            PieceType.ROOK -> {
                sourceSquare.pieceType = pieceType
                movePiece(sourceSquare, destinationSquare)
            }
            PieceType.BISHOP -> {
                sourceSquare.pieceType = pieceType
                movePiece(sourceSquare, destinationSquare)
            }
            PieceType.KNIGHT -> {
                sourceSquare.pieceType = pieceType
                movePiece(sourceSquare, destinationSquare)
            }
            else -> throw IllegalArgumentException("Unexpected PieceType: $pieceType")
        }
    }
}