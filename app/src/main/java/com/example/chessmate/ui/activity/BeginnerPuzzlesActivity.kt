package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R

class BeginnerPuzzlesActivity : AbsThemeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beginner_puzzles)

        val toolbar = findViewById<Toolbar>(R.id.beginner_puzzles_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}
    }
}