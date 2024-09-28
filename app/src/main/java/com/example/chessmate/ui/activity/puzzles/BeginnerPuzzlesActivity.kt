package com.example.chessmate.ui.activity.puzzles

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.chessmate.R
import com.example.chessmate.adapter.PuzzlesPagerAdapter
import com.example.chessmate.database.PuzzleCompletionRepository
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.JSONParser
import com.example.chessmate.util.Puzzle
import com.example.chessmate.util.UserProfileManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class BeginnerPuzzlesActivity : AbsThemeActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var toolbar : Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: PuzzlesPagerAdapter
    private lateinit var puzzleCompletionRepository: PuzzleCompletionRepository
    private var beginnerCompletedPuzzles: List<Int> = emptyList()
    private val userProfileManager = UserProfileManager.getInstance()
    private var userProfile: UserProfile? = null
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beginner_puzzles)

        puzzleCompletionRepository = PuzzleCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        bottomNavigationView = findViewById(R.id.beginner_puzzle_bottom_navigation)
        viewPager2 = findViewById(R.id.beginner_puzzles_view_pager)
        val pageIndicator = findViewById<TextView>(R.id.beginner_puzzles_page_indicator)

        val jsonString = jsonParser.loadJSONFromAsset(this, "beginner_puzzles.json")
        val puzzles: List<Puzzle> = Gson().fromJson(jsonString, object : TypeToken<List<Puzzle>>() {}.type)

        val paginatedPuzzles = dividePuzzlesIntoPages(puzzles)

        if (userProfile != null) {
            lifecycleScope.launch {
                beginnerCompletedPuzzles = preloadCompletedBeginnerPuzzles()

                adapter = PuzzlesPagerAdapter(paginatedPuzzles, puzzles, beginnerCompletedPuzzles, this@BeginnerPuzzlesActivity)
                viewPager2.adapter = adapter

                viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        pageIndicator.text = getString(R.string.page_indicator_text, position + 1, adapter.itemCount)
                        updateBottomNavigationState(position)
                    }
                })
            }
        }

        toolbar = findViewById(R.id.beginner_puzzles_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            beginnerCompletedPuzzles = preloadCompletedBeginnerPuzzles()
            adapter.updateCompletedPuzzles(beginnerCompletedPuzzles)
        }
    }

    private fun dividePuzzlesIntoPages(puzzles: List<Puzzle>): List<List<Puzzle>> {
        return puzzles.chunked(10)
    }

    private suspend fun preloadCompletedBeginnerPuzzles(): List<Int> {
        return puzzleCompletionRepository.getAllBeginnerPuzzlesId(userProfile!!.userID)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.puzzle_previous_page -> {
                    if (viewPager2.currentItem > 0) {
                        viewPager2.currentItem -= 1
                    }
                    true
                }
                R.id.puzzle_next_page -> {
                    if (viewPager2.currentItem < adapter.itemCount - 1) {
                        viewPager2.currentItem += 1
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun updateBottomNavigationState(position: Int) {
        val previousMenuItem = bottomNavigationView.menu.findItem(R.id.puzzle_previous_page)
        val nextMenuItem = bottomNavigationView.menu.findItem(R.id.puzzle_next_page)

        previousMenuItem.isEnabled = position > 0
        nextMenuItem.isEnabled = position < adapter.itemCount - 1
    }
}