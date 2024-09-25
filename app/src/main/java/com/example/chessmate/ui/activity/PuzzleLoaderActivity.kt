package com.example.chessmate.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.util.Puzzle
import com.google.android.material.bottomnavigation.BottomNavigationView

class PuzzleLoaderActivity : AbsThemeActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var puzzle: Puzzle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_loader)

        puzzle = intent.getParcelableExtra("selectedPuzzle")!!

        bottomNavigationView = findViewById(R.id.puzzle_loader_bottom_navigation)

        toolbar = findViewById(R.id.puzzle_loader_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        setupTextViews()

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun setupTextViews() {
        val difficultyTextView = findViewById<TextView>(R.id.current_puzzle_difficulty)
        val puzzleIDTextView = findViewById<TextView>(R.id.current_puzzle_id)

        difficultyTextView.text = when (puzzle.difficulty) {
            1 -> getString(R.string.beginner_level)
            2 -> getString(R.string.intermediate_level)
            3 -> getString(R.string.advanced_level)
            else -> ""
        }

        val result = getString(R.string.puzzle_text) + " " + puzzle.puzzleId.toString()
        puzzleIDTextView.text = result
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.previous_puzzle -> {
                println("previous puzzle button clicked")
                return true
            }

            R.id.puzzle_hint -> {
                println("hint puzzle button clicked")
                return true
            }

            R.id.next_puzzle -> {
                println("next puzzle button clicked")
                return true
            }

            else -> return false
        }
    }
}