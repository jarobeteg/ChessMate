package com.example.chessmate.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chessmate.util.AppThemeUtil

abstract class AbsThemeActivity: AppCompatActivity() {
    private var themeStyle: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
    }

    override fun onResume() {
        super.onResume()
        val newThemeStyle: Int = AppThemeUtil(this).setThemeType()

        if (themeStyle != newThemeStyle) {
            themeStyle = newThemeStyle
            setTheme(themeStyle)
            recreate()
        }
    }

    fun updateTheme(){
        themeStyle = AppThemeUtil(this).setThemeType()
        setTheme(themeStyle)
    }
}