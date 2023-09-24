package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chessmate.util.ThemeUtil

abstract class AbsThemeActivity: AppCompatActivity() {
    private var themeStyle: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
    }

    override fun onResume() {
        super.onResume()
        val newThemeStyle: Int = ThemeUtil(this).setThemeType()

        if (themeStyle != newThemeStyle) {
            themeStyle = newThemeStyle
            setTheme(themeStyle)
            recreate()
        }
    }

    fun updateTheme(){
        themeStyle = ThemeUtil(this).setThemeType()
        setTheme(themeStyle)
    }
}