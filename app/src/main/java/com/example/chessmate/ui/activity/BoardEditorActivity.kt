package com.example.chessmate.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.Position
import com.example.chessmate.util.chess.bitboard.BitPiece
import com.example.chessmate.util.chess.bitboard.BitSquare
import com.example.chessmate.util.chess.bitboard.Bitboard
import com.example.chessmate.util.chess.bitboard.BitboardUIMapper

class BoardEditorActivity : AbsThemeActivity() {
    private lateinit var chessboardLayout: GridLayout
    private lateinit var board: Bitboard
    private lateinit var uiMapper: BitboardUIMapper
    private lateinit var uiSquares: Array<Array<FrameLayout>>
    private lateinit var remove: ImageButton
    private lateinit var whitePawn: ImageButton
    private lateinit var whiteKnight: ImageButton
    private lateinit var whiteBishop: ImageButton
    private lateinit var whiteRook: ImageButton
    private lateinit var whiteQueen: ImageButton
    private lateinit var whiteKing: ImageButton
    private lateinit var blackPawn: ImageButton
    private lateinit var blackKnight: ImageButton
    private lateinit var blackBishop: ImageButton
    private lateinit var blackRook: ImageButton
    private lateinit var blackQueen: ImageButton
    private lateinit var blackKing: ImageButton
    private lateinit var whiteKingSideCastles: CheckBox
    private lateinit var whiteQueenSideCastles: CheckBox
    private lateinit var blackKingSideCastles: CheckBox
    private lateinit var blackQueenSideCastles: CheckBox
    private lateinit var whoToPlay: RadioGroup
    private lateinit var chessThemeUtil: ChessThemeUtil
    private var lightSquareColor = R.color.default_light_square_color
    private var darkSquareColor = R.color.default_dark_square_color
    private var pieceThemeArray = IntArray(12)
    private var editor = 0
    private var squareSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_editor)

        val toolbar = findViewById<Toolbar>(R.id.board_editor_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val copyFEN = findViewById<ImageButton>(R.id.copy_fen_string)
        copyFEN.setOnClickListener { copyFEN() }

        chessThemeUtil = ChessThemeUtil(this)

        val boardTheme = chessThemeUtil.getBoardTheme()
        lightSquareColor = boardTheme.first
        darkSquareColor = boardTheme.second

        pieceThemeArray = chessThemeUtil.getPieceTheme()

        remove = findViewById(R.id.editor_remove)
        whitePawn = findViewById(R.id.editor_white_pawn)
        whiteKnight = findViewById(R.id.editor_white_knight)
        whiteBishop = findViewById(R.id.editor_white_bishop)
        whiteRook = findViewById(R.id.editor_white_rook)
        whiteQueen = findViewById(R.id.editor_white_queen)
        whiteKing = findViewById(R.id.editor_white_king)
        blackPawn = findViewById(R.id.editor_black_pawn)
        blackKnight = findViewById(R.id.editor_black_knight)
        blackBishop = findViewById(R.id.editor_black_bishop)
        blackRook = findViewById(R.id.editor_black_rook)
        blackQueen = findViewById(R.id.editor_black_queen)
        blackKing = findViewById(R.id.editor_black_king)

        setEditorResourceId()

        remove.setOnClickListener { handleEditorClick(BitPiece.NONE) }
        whitePawn.setOnClickListener { handleEditorClick(BitPiece.WHITE_PAWN) }
        whiteKnight.setOnClickListener { handleEditorClick(BitPiece.WHITE_KNIGHT) }
        whiteBishop.setOnClickListener { handleEditorClick(BitPiece.WHITE_BISHOP) }
        whiteRook.setOnClickListener { handleEditorClick(BitPiece.WHITE_ROOK) }
        whiteQueen.setOnClickListener { handleEditorClick(BitPiece.WHITE_QUEEN) }
        whiteKing.setOnClickListener { handleEditorClick(BitPiece.WHITE_KING) }
        blackPawn.setOnClickListener { handleEditorClick(BitPiece.BLACK_PAWN) }
        blackKnight.setOnClickListener { handleEditorClick(BitPiece.BLACK_KNIGHT) }
        blackBishop.setOnClickListener { handleEditorClick(BitPiece.BLACK_BISHOP) }
        blackRook.setOnClickListener { handleEditorClick(BitPiece.BLACK_ROOK) }
        blackQueen.setOnClickListener { handleEditorClick(BitPiece.BLACK_QUEEN) }
        blackKing.setOnClickListener { handleEditorClick(BitPiece.BLACK_KING) }

        whiteKingSideCastles = findViewById(R.id.white_king_side_castle)
        whiteQueenSideCastles = findViewById(R.id.white_queen_side_castle)
        blackKingSideCastles = findViewById(R.id.black_king_side_castle)
        blackQueenSideCastles = findViewById(R.id.black_queen_side_castle)
        whoToPlay = findViewById(R.id.who_to_play)

        chessboardLayout = findViewById(R.id.bitboard)
        board = Bitboard()
        uiMapper = BitboardUIMapper()

        board.startingPosition()
        initializeBitboardUI()
        setupInitialBoardUI()
        setupSquareListener()
    }

    private fun handleEditorClick(piece: BitPiece) {
        unselectEditors()
        when (piece) {
            BitPiece.NONE -> {
                remove.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 0
            }
            BitPiece.WHITE_PAWN -> {
                whitePawn.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 1
            }
            BitPiece.WHITE_KNIGHT -> {
                whiteKnight.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 2
            }
            BitPiece.WHITE_BISHOP -> {
                whiteBishop.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 3
            }
            BitPiece.WHITE_ROOK -> {
                whiteRook.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 4
            }
            BitPiece.WHITE_QUEEN -> {
                whiteQueen.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 5
            }
            BitPiece.WHITE_KING -> {
                whiteKing.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 6
            }
            BitPiece.BLACK_PAWN -> {
                blackPawn.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 7
            }
            BitPiece.BLACK_KNIGHT -> {
                blackKnight.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 8
            }
            BitPiece.BLACK_BISHOP -> {
                blackBishop.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 9
            }
            BitPiece.BLACK_ROOK -> {
                blackRook.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 10
            }
            BitPiece.BLACK_QUEEN -> {
                blackQueen.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 11
            }
            BitPiece.BLACK_KING -> {
                blackKing.setBackgroundResource(R.drawable.custom_board_editor_button)
                editor = 12
            }
        }
    }

    private fun handleSquareClick(square: BitSquare) {
        when (editor) {
            0 -> removePiece(square.piece, square.position)
            1 -> addPiece(square.piece, BitPiece.WHITE_PAWN, square.position)
            2 -> addPiece(square.piece, BitPiece.WHITE_KNIGHT, square.position)
            3 -> addPiece(square.piece, BitPiece.WHITE_BISHOP, square.position)
            4 -> addPiece(square.piece, BitPiece.WHITE_ROOK, square.position)
            5 -> addPiece(square.piece, BitPiece.WHITE_QUEEN, square.position)
            6 -> addPiece(square.piece, BitPiece.WHITE_KING, square.position)
            7 -> addPiece(square.piece, BitPiece.BLACK_PAWN, square.position)
            8 -> addPiece(square.piece, BitPiece.BLACK_KNIGHT, square.position)
            9 -> addPiece(square.piece, BitPiece.BLACK_BISHOP, square.position)
            10 -> addPiece(square.piece, BitPiece.BLACK_ROOK, square.position)
            11 -> addPiece(square.piece, BitPiece.BLACK_QUEEN, square.position)
            12 -> addPiece(square.piece, BitPiece.BLACK_KING, square.position)
        }
        updateBitboardStateUI()
    }

    private fun removePiece(piece: BitPiece, position: Long) {
        board.removePiece(piece, position)
    }

    private fun addPiece(removePiece: BitPiece, addPiece: BitPiece, position: Long) {
        board.removePiece(removePiece, position)
        board.setPiece(addPiece, position)
    }

    private fun copyFEN() {
        val fenString = makeFEN()
        val fen = FEN(fenString)
        if (fen.isValid) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FEN", fenString)
            clipboard.setPrimaryClip(clip)
        } else {
            Toast.makeText(this, getString(R.string.illegal_board_position_in_editor), Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeFEN(): String {
        val builder = StringBuilder()

        for (rank in 7 downTo 0) {
            var emptySquares = 0
            for (file in 0 until 8) {
                val squareIndex = rank * 8 + file
                val square = 1L shl squareIndex
                val piece = board.getPiece(square)

                if (piece.piece == BitPiece.NONE) {
                    emptySquares++
                } else {
                    if (emptySquares > 0) {
                        builder.append(emptySquares)
                        emptySquares = 0
                    }
                    val pieceChar = when (piece.piece) {
                        BitPiece.WHITE_PAWN -> 'P'
                        BitPiece.WHITE_KNIGHT -> 'N'
                        BitPiece.WHITE_BISHOP -> 'B'
                        BitPiece.WHITE_ROOK -> 'R'
                        BitPiece.WHITE_QUEEN -> 'Q'
                        BitPiece.WHITE_KING -> 'K'
                        BitPiece.BLACK_PAWN -> 'p'
                        BitPiece.BLACK_KNIGHT -> 'n'
                        BitPiece.BLACK_BISHOP -> 'b'
                        BitPiece.BLACK_ROOK -> 'r'
                        BitPiece.BLACK_QUEEN -> 'q'
                        BitPiece.BLACK_KING -> 'k'
                        else -> throw IllegalStateException("Invalid piece")
                    }
                    builder.append(pieceChar)
                }
            }
            if (emptySquares > 0) builder.append(emptySquares)
            if (rank > 0) builder.append('/')
        }

        builder.append(" ").append(if (whoToPlay.checkedRadioButtonId == R.id.white_to_play) 'w' else 'b')

        builder.append(" ")
        var castling = ""
        if (whiteKingSideCastles.isChecked) castling += "K"
        if (whiteQueenSideCastles.isChecked) castling += "Q"
        if (blackKingSideCastles.isChecked) castling += "k"
        if (blackQueenSideCastles.isChecked) castling += "q"
        if (castling.isEmpty()) castling = "-"
        builder.append(castling)

        builder.append(" ")
        builder.append("-")
        builder.append(" ").append(0)
        builder.append(" ").append(1)

        return builder.toString()
    }

    private fun unselectEditors() {
        remove.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whitePawn.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whiteKnight.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whiteBishop.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whiteRook.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whiteQueen.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        whiteKing.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackPawn.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackKnight.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackBishop.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackRook.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackQueen.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
        blackKing.setBackgroundResource(R.drawable.custom_board_editor_unselected_button)
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

    private fun setupInitialBoardUI() {
        for (row in 7 downTo 0) {
            for (col in 0..7) {
                val position = 1L shl ((7 - row) * 8 + col)
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
                val position = 1L shl ((7 - row) * 8 + col)
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
                val position = 1L shl ((7 - row) * 8 + col)
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
            val number = (8 - row).toString()
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
            val letter = ('a' + col).toString()
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

    private fun setEditorResourceId() {
        whitePawn.setImageResource(pieceThemeArray[0])
        whiteKnight.setImageResource(pieceThemeArray[1])
        whiteBishop.setImageResource(pieceThemeArray[2])
        whiteRook.setImageResource(pieceThemeArray[3])
        whiteQueen.setImageResource(pieceThemeArray[4])
        whiteKing.setImageResource(pieceThemeArray[5])
        blackPawn.setImageResource(pieceThemeArray[6])
        blackKnight.setImageResource(pieceThemeArray[7])
        blackBishop.setImageResource(pieceThemeArray[8])
        blackRook.setImageResource(pieceThemeArray[9])
        blackQueen.setImageResource(pieceThemeArray[10])
        blackKing.setImageResource(pieceThemeArray[11])
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
}