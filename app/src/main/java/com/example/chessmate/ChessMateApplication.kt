package com.example.chessmate

import android.app.Application
import com.example.chessmate.database.ChessMateDatabase

class ChessMateApplication: Application() {
    val database by lazy { ChessMateDatabase.getDatabase(this) }
}