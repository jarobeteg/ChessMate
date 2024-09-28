package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CoordinatesLessonActivity : AbsThemeActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var settings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinates_lesson)

        bottomNavigationView = findViewById(R.id.coordinates_lesson_bottom_navigation)

        toolbar = findViewById(R.id.coordinates_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        settings = findViewById(R.id.coordinates_lesson_settings)
        settings.setOnClickListener { showSettingsDialog() }

        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener bottomNavItemClicked(item)
        }
    }

    private fun showSettingsDialog() {
        println("settings dialog")
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.stop_coordinates -> {
                println("stop coordinates")
                return true
            }

            R.id.play_coordinates -> {
                println("play coordinates")
                return true
            }

            else -> return false
        }
    }
}