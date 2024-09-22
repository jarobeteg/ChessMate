package com.example.chessmate.util

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import androidx.preference.PreferenceManager
import com.example.chessmate.R

class ChessThemeUtil (private var context: Context) {
    private var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val boardThemeWood = "wood"
    private val boardThemeMarble = "marble"
    private val boardThemeModern = "modern"

    private val pieceThemeFresca = "fresca"
    private val pieceThemeGovernor = "governor"
    private val pieceThemePixel = "pixel"

    fun getBoardTheme(): Pair<Int, Int> {
        val newBoardThemeKey = context.getString(R.string.pref_chessboard_theme_key)
        val defaultBoardThemeValue = context.getString(R.string.default_chessboard_theme_value)
        val newBoardTheme = sharedPref.getString(newBoardThemeKey, defaultBoardThemeValue) ?: defaultBoardThemeValue

        val boardTheme: Pair<Int, Int> = when (newBoardTheme) {
            boardThemeWood -> getBoardThemeWood()
            boardThemeMarble -> getBoardThemeMarble()
            boardThemeModern -> getBoardThemeModern()
            else -> getBoardThemePlain()
        }
        return boardTheme
    }

    private fun getBoardThemePlain(): Pair<Int, Int> {
        val lightSquare = R.color.default_light_square_color
        val darkSquare = R.color.default_dark_square_color
        return Pair(lightSquare, darkSquare)
    }

    private fun getBoardThemeWood(): Pair<Int, Int> {
        val lightSquare = R.color.wood_light_square_color
        val darkSquare = R.color.wood_dark_square_color
        return Pair(lightSquare, darkSquare)
    }

    private fun getBoardThemeMarble(): Pair<Int, Int> {
        val lightSquare = R.color.marble_light_square_color
        val darkSquare = R.color.marble_dark_square_color
        return Pair(lightSquare, darkSquare)
    }

    private fun getBoardThemeModern(): Pair<Int, Int> {
        val lightSquare = getThemeColorResId(R.attr.colorLightSquare)
        val darkSquare = getThemeColorResId(R.attr.colorDarkSquare)
        return Pair(lightSquare, darkSquare)
    }

    private fun getThemeColorResId(attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.resourceId
    }

    fun getPieceTheme(): IntArray {
        val newPieceThemeKey = context.getString(R.string.pref_piece_theme_key)
        val defaultPieceThemeValue = context.getString(R.string.default_piece_theme_value)
        val newPieceTheme = sharedPref.getString(newPieceThemeKey, defaultPieceThemeValue) ?: defaultPieceThemeValue

        val pieceTheme: IntArray = when (newPieceTheme) {
            pieceThemeFresca -> getPieceThemeFresca()
            pieceThemeGovernor -> getPieceThemeGovernor()
            pieceThemePixel -> getPieceThemePixel()
            else -> getPieceThemePlain()
        }
        return pieceTheme
    }

    private fun getPieceThemePlain(): IntArray {
        return intArrayOf(
            R.drawable.piece_default_pawn_white,
            R.drawable.piece_default_knight_white,
            R.drawable.piece_default_bishop_white,
            R.drawable.piece_default_rook_white,
            R.drawable.piece_default_queen_white,
            R.drawable.piece_default_king_white,
            R.drawable.piece_default_pawn_black,
            R.drawable.piece_default_knight_black,
            R.drawable.piece_default_bishop_black,
            R.drawable.piece_default_rook_black,
            R.drawable.piece_default_queen_black,
            R.drawable.piece_default_king_black
        )
    }

    private fun getPieceThemeFresca(): IntArray {
        return intArrayOf(
            R.drawable.piece_fresca_pawn_white,
            R.drawable.piece_fresca_knight_white,
            R.drawable.piece_fresca_bishop_white,
            R.drawable.piece_fresca_rook_white,
            R.drawable.piece_fresca_queen_white,
            R.drawable.piece_fresca_king_white,
            R.drawable.piece_fresca_pawn_black,
            R.drawable.piece_fresca_knight_black,
            R.drawable.piece_fresca_bishop_black,
            R.drawable.piece_fresca_rook_black,
            R.drawable.piece_fresca_queen_black,
            R.drawable.piece_fresca_king_black
        )
    }

    private fun getPieceThemeGovernor(): IntArray {
        return intArrayOf(
            R.drawable.piece_governor_pawn_white,
            R.drawable.piece_governor_knight_white,
            R.drawable.piece_governor_bishop_white,
            R.drawable.piece_governor_rook_white,
            R.drawable.piece_governor_queen_white,
            R.drawable.piece_governor_king_white,
            R.drawable.piece_governor_pawn_black,
            R.drawable.piece_governor_knight_black,
            R.drawable.piece_governor_bishop_black,
            R.drawable.piece_governor_rook_black,
            R.drawable.piece_governor_queen_black,
            R.drawable.piece_governor_king_black
        )
    }

    private fun getPieceThemePixel(): IntArray {
        return intArrayOf(
            R.drawable.piece_pixel_pawn_white,
            R.drawable.piece_pixel_knight_white,
            R.drawable.piece_pixel_bishop_white,
            R.drawable.piece_pixel_rook_white,
            R.drawable.piece_pixel_queen_white,
            R.drawable.piece_pixel_king_white,
            R.drawable.piece_pixel_pawn_black,
            R.drawable.piece_pixel_knight_black,
            R.drawable.piece_pixel_bishop_black,
            R.drawable.piece_pixel_rook_black,
            R.drawable.piece_pixel_queen_black,
            R.drawable.piece_pixel_king_black
        )
    }
}