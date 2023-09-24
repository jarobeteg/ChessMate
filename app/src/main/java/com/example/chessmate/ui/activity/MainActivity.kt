package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chessmate.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AbsThemeActivity() {
    private lateinit var navController : NavController
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var mainToolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        mainToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()){
            super.onBackPressed()
        }
    }
}