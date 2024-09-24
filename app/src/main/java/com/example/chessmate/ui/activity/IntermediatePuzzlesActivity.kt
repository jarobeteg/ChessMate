package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.chessmate.R
import com.example.chessmate.adapter.PuzzlesPagerAdapter
import com.example.chessmate.util.JSONParser
import com.google.android.material.bottomnavigation.BottomNavigationView

class IntermediatePuzzlesActivity : AbsThemeActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: PuzzlesPagerAdapter
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intermediate_puzzles)

        bottomNavigationView = findViewById(R.id.intermediate_puzzle_bottom_navigation)
        viewPager2 = findViewById(R.id.intermediate_puzzles_view_pager)

        toolbar = findViewById(R.id.intermediate_puzzles_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}
    }
}