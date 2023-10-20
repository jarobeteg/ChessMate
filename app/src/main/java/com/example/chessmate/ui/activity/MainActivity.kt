package com.example.chessmate.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.example.chessmate.R
import com.example.chessmate.adapter.MainViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AbsThemeActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var mainToolbar : Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        viewPager2 = findViewById(R.id.mainViewPager)

        mainToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter = MainViewPagerAdapter(this)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
                updateToolbarTitle(position)
            }
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_fragment_home -> viewPager2.setCurrentItem(0, true)
                R.id.nav_fragment_puzzles -> viewPager2.setCurrentItem(1, true)
                R.id.nav_fragment_learn -> viewPager2.setCurrentItem(2, true)
                R.id.nav_fragment_profile -> viewPager2.setCurrentItem(3, true)
                R.id.nav_fragment_more -> viewPager2.setCurrentItem(4, true)
            }
            true
        }
    }

    override fun onBackPressed() {
        if (viewPager2.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager2.setCurrentItem(0, true)
        }
    }

    private fun updateToolbarTitle(position: Int) {
        val newTitle = when (position) {
            0 -> getString(R.string.app_name)
            1 -> getString(R.string.bottom_nav_puzzles)
            2 -> getString(R.string.bottom_nav_learn)
            3 -> getString(R.string.bottom_nav_profile)
            4 -> getString(R.string.bottom_nav_more)
            else -> getString(R.string.app_name)
        }
        val textView: TextView? = findViewById(R.id.main_toolbar_title)
        textView?.text = newTitle
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putBoolean("first_time", false)
        editor.apply()
    }
}