package com.example.chessmate.util

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private val min: Int, private val max: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest?.toString() + source?.toString()).toInt()
            if (isInRange(min, max, input)) {
                return null
            }
        } catch (_: NumberFormatException) {}
        return ""
    }

    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return value in min..max
    }
}