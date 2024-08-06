package com.example.chessmate.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R

class PlayActivity : AbsThemeActivity() {
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
        val startGameButton = findViewById<Button>(R.id.startGameChess)

        val sharedPreferences = getSharedPreferences("chess_game", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("starting_side", "random")

        levelSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                depth = progress
                editor.putInt("depth", depth)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        randomTextView.setOnClickListener {
            randomTextView.setBackgroundResource(R.drawable.custom_rounded_textview)
            whiteTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            blackTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            editor.putString("starting_side", "random")
        }

        whiteTextView.setOnClickListener {
            randomTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            whiteTextView.setBackgroundResource(R.drawable.custom_rounded_textview)
            blackTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            editor.putString("starting_side", "white")
        }

        blackTextView.setOnClickListener {
            randomTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            whiteTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            blackTextView.setBackgroundResource(R.drawable.custom_rounded_textview)
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

            editor.apply()
            val playIntent = Intent(this, BitboardActivity::class.java)
            startActivity(playIntent)
        }
    }
}