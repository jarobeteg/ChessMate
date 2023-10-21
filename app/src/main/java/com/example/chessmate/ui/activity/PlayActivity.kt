package com.example.chessmate.ui.activity

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R

class PlayActivity : AbsThemeActivity() {
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

        levelSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
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
        }

        whiteTextView.setOnClickListener {
            randomTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            whiteTextView.setBackgroundResource(R.drawable.custom_rounded_textview)
            blackTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
        }

        blackTextView.setOnClickListener {
            randomTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            whiteTextView.setBackgroundResource(R.drawable.custom_rounded_unselected_textview)
            blackTextView.setBackgroundResource(R.drawable.custom_rounded_textview)
        }
    }
}