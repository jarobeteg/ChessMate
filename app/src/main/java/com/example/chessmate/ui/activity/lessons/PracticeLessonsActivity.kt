package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R
import com.example.chessmate.ui.activity.AbsThemeActivity

class PracticeLessonsActivity : AbsThemeActivity() {
    private lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_lessons)

        toolbar = findViewById(R.id.practice_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}
    }
}