package com.example.chessmate.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.util.chess.FEN
import com.example.chessmate.util.chess.FENHolder
import com.example.chessmate.util.chess.GameContext
import com.example.chessmate.util.chess.PieceColor

class PlayActivity : AbsThemeActivity() {
    private lateinit var fenEditText: EditText
    private var playerColor: PieceColor = PieceColor.WHITE
    private var selectedSide: String = "random"
    private var depth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val toolbar = findViewById<Toolbar>(R.id.play_chess_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        val levelSlider = findViewById<SeekBar>(R.id.level_slider)
        val randomTextView = findViewById<TextView>(R.id.random_textview)
        val whiteTextView = findViewById<TextView>(R.id.white_textview)
        val blackTextView = findViewById<TextView>(R.id.black_textview)
        fenEditText = findViewById(R.id.fen_edit_text)
        val startGameButton = findViewById<Button>(R.id.startGameChess)

        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        playerColor = randomStartingSide()
        editor.putString("starting_side", if (playerColor == PieceColor.WHITE) "white" else "black")

        levelSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                depth = progress
                editor.putInt("depth", depth)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        randomTextView.setOnClickListener {
            playerColor = randomStartingSide()
            selectedSide = "random"
            updateTextViewBackgrounds(randomTextView, whiteTextView, blackTextView, selectedSide)
            editor.putString("starting_side", if (playerColor == PieceColor.WHITE) "white" else "black")
        }

        whiteTextView.setOnClickListener {
            playerColor = PieceColor.WHITE
            selectedSide = "white"
            updateTextViewBackgrounds(randomTextView, whiteTextView, blackTextView, selectedSide)
            editor.putString("starting_side", "white")
        }

        blackTextView.setOnClickListener {
            playerColor = PieceColor.BLACK
            selectedSide = "black"
            updateTextViewBackgrounds(randomTextView, whiteTextView, blackTextView, selectedSide)
            editor.putString("starting_side", "black")
        }

        startGameButton.setOnClickListener {
            startGameButton.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    startGameButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            val fenString = fenEditText.text.toString()
            if (fenString.isEmpty() || fenString.isBlank()) {
                editor.apply()
                val playIntent = Intent(this, BitboardActivity::class.java)
                startActivity(playIntent)
                return@setOnClickListener
            }

            GameContext.playerColor = playerColor
            GameContext.botColor = playerColor.opposite()
            val fen = FEN(fenString)

            if (fen.isValid) {
                editor.apply()
                FENHolder.setFEN(fen)
                val playIntent = Intent(this, BitboardActivity::class.java)
                startActivity(playIntent)
            } else {
                Toast.makeText(this, getString(fen.validationMessage), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FENHolder.setFEN(null)
        if (selectedSide == "random") {
            val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            playerColor = randomStartingSide()
            editor.putString("starting_side", if (playerColor == PieceColor.WHITE) "white" else "black")
            editor.apply()
        }
    }

    private fun randomStartingSide(): PieceColor {
        return if (java.util.Random().nextBoolean()) {
            PieceColor.WHITE
        } else {
            PieceColor.BLACK
        }
    }

    private fun updateTextViewBackgrounds(randomTextView: TextView, whiteTextView: TextView, blackTextView: TextView, selectedSide: String) {
        randomTextView.setBackgroundResource(if (selectedSide == "random") R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
        whiteTextView.setBackgroundResource(if (selectedSide == "white") R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
        blackTextView.setBackgroundResource(if (selectedSide == "black") R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
    }
}