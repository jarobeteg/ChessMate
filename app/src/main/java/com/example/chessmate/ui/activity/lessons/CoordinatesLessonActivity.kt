package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.chess.CoordinateSettingsDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class CoordinatesLessonActivity : AbsThemeActivity(), CoordinateSettingsDialogFragment.OnSaveButtonListener {
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var countdownTimerTextView: TextView
    private var timeLimit: Long = 15000
    private var timeLeftInMillis: Long = timeLimit
    private var timerRunning = false
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var settings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinates_lesson)

        countdownTimerTextView = findViewById(R.id.countdown_timer)
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

        updateCountdownText()
    }

    override fun onSaveButtonListener(showCoordinates: Boolean, showPieces: Boolean, playAsWhite: Boolean, minutes: Int, seconds: Int) {
        timeLimit = if (seconds < 5 && minutes == 0) {
            5000
        } else {
            (minutes * 60 * 1000 + seconds * 1000).toLong()
        }
        timeLeftInMillis = timeLimit

        updateCountdownText()
    }

    private fun showSettingsDialog() {
        val dialog = CoordinateSettingsDialogFragment()
        dialog.show(supportFragmentManager, "CoordinateSettingsDialog")
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                resetTimer()
            }
        }.start()

        timerRunning = true
    }

    private fun stopTimer() {
        countdownTimer.cancel()
        resetTimer()
    }

    private fun resetTimer() {
        timerRunning = false
        timeLeftInMillis = timeLimit
        updateCountdownText()
    }

    private fun updateCountdownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)
        countdownTimerTextView.text = timeFormatted
    }

    private fun bottomNavItemClicked(item: MenuItem): Boolean{
        return when (item.itemId) {
            R.id.stop_coordinates -> {
                if (timerRunning) {
                    stopTimer()
                }
                true
            }

            R.id.play_coordinates -> {
                if (!timerRunning) {
                    startTimer()
                }
                true
            }

            else -> false
        }
    }
}