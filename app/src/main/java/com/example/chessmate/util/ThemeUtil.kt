package com.example.chessmate.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.example.chessmate.R

class ThemeUtil (context: Context){
    private var context: Context

    private lateinit var sharedPref: SharedPreferences

    private val themeDay = "day"
    private val themeNight = "night"
    private val themeDayNight = "day_night"

    init {
        this.context = context
    }

    fun setThemeType(): Int{
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        val newThemeKey = context.getString(R.string.pref_theme_key)
        val defaultThemeValue = context.getString(R.string.default_theme_value)
        val newTheme = sharedPref.getString(newThemeKey, defaultThemeValue) ?: defaultThemeValue

        val newColorKey = context.getString(R.string.pref_color_key)
        val defaultColorValue = context.getString(R.string.default_color_value)
        val newColor = sharedPref.getString(newColorKey, defaultColorValue) ?: defaultColorValue

        return if (newTheme == themeDay) {
            dayTheme(newColor)
        } else if (newTheme == themeNight) {
            nightTheme(newColor)
        } else if (newTheme == themeDayNight) {
            if (dayOrNight()) {
                nightTheme(newColor)
            } else {
                dayTheme(newColor)
            }
        }else{
            R.style.Theme_ChessMate_Light_Teal
        }
    }

    private fun dayTheme(currentColor: String): Int{
        return when (currentColor){
            "teal" -> R.style.Theme_ChessMate_Light_Teal
            "indigo" -> R.style.Theme_ChessMate_Light_Indigo
            "dark_purple" -> R.style.Theme_ChessMate_Light_DarkPurple
            "blue" -> R.style.Theme_ChessMate_Light_Blue
            "green" -> R.style.Theme_ChessMate_Light_Green
            "amber" -> R.style.Theme_ChessMate_Light_Amber
            "red" -> R.style.Theme_ChessMate_Light_Red
            "pink" -> R.style.Theme_ChessMate_Light_Pink
            else -> R.style.Theme_ChessMate_Light_Teal
        }
    }

    private fun nightTheme(currentColor: String): Int{
        return when (currentColor){
            "teal" -> R.style.Theme_ChessMate_Dark_Teal
            "indigo" -> R.style.Theme_ChessMate_Dark_Indigo
            "dark_purple" -> R.style.Theme_ChessMate_Dark_DarkPurple
            "blue" -> R.style.Theme_ChessMate_Dark_Blue
            "green" -> R.style.Theme_ChessMate_Dark_Green
            "amber" -> R.style.Theme_ChessMate_Dark_Amber
            "red" -> R.style.Theme_ChessMate_Dark_Red
            "pink" -> R.style.Theme_ChessMate_Dark_Pink
            else -> R.style.Theme_ChessMate_Light_Teal
        }
    }
    private fun dayOrNight(): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}