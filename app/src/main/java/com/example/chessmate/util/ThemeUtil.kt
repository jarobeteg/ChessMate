package com.example.chessmate.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.example.chessmate.R

class ThemeUtil (context: Context){
    private var context: Context
    private var selectedTheme: Int = -1

    private lateinit var sharedPref: SharedPreferences

    private val themeDay = "day"
    private val themeNight = "night"
    private val themeDayNight = "day_night"

    init {
        this.context = context
    }

    fun setThemeType(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        val newThemeKey = context.getString(R.string.pref_theme_key)
        val defaultThemeValue = context.getString(R.string.default_theme_value)
        val newTheme = sharedPref.getString(newThemeKey, defaultThemeValue) ?: defaultThemeValue

        val newColorKey = context.getString(R.string.pref_color_key)
        val defaultColorValue = context.getString(R.string.default_color_value)
        val newColor = sharedPref.getString(newColorKey, defaultColorValue) ?: defaultColorValue

        if (newTheme == themeDay) {
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
            defaultTheme()
        }
    }

    private fun dayTheme(currentColor: String){
        when (currentColor){
            "teal" -> selectedTheme = R.style.Theme_ChessMate_Light_Teal
            "indigo" -> selectedTheme = R.style.Theme_ChessMate_Light_Indigo
            "purple" -> selectedTheme = R.style.Theme_ChessMate_Light_Purple
            "blue" -> selectedTheme = R.style.Theme_ChessMate_Light_Blue
            "green" -> selectedTheme = R.style.Theme_ChessMate_Light_Green
            "amber" -> selectedTheme = R.style.Theme_ChessMate_Light_Amber
            "red" -> selectedTheme = R.style.Theme_ChessMate_Light_Red
            "orange" -> selectedTheme = R.style.Theme_ChessMate_Light_Orange
            else -> defaultTheme()
        }
        setSelectedTheme()
    }

    private fun nightTheme(currentColor: String){
        when (currentColor){
            "teal" -> selectedTheme = R.style.Theme_ChessMate_Dark_Teal
            "indigo" -> selectedTheme = R.style.Theme_ChessMate_Dark_Indigo
            "purple" -> selectedTheme = R.style.Theme_ChessMate_Dark_Purple
            "blue" -> selectedTheme = R.style.Theme_ChessMate_Dark_Blue
            "green" -> selectedTheme = R.style.Theme_ChessMate_Dark_Green
            "amber" -> selectedTheme = R.style.Theme_ChessMate_Dark_Amber
            "red" -> selectedTheme = R.style.Theme_ChessMate_Dark_Red
            "orange" -> selectedTheme = R.style.Theme_ChessMate_Dark_Orange
            else -> defaultTheme()
        }
        setSelectedTheme()
    }

    private fun defaultTheme(){
        selectedTheme = R.style.Theme_ChessMate_Light_Teal
        setSelectedTheme()
    }

    private fun setSelectedTheme(){
        val editor = sharedPref.edit()
        editor.putInt("selectedTheme", selectedTheme)
        editor.apply()
    }

    private fun dayOrNight(): Boolean{
        return context.resources.configuration.uiMode == Configuration.UI_MODE_NIGHT_MASK
    }
}