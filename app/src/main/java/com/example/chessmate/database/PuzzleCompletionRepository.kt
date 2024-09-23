package com.example.chessmate.database

import android.content.Context
import com.example.chessmate.database.dao.PuzzleCompletionDAO

class PuzzleCompletionRepository(private val context: Context) {

    private val database: ChessMateDatabase by lazy {
        ChessMateDatabase.getDatabase(context)
    }

    private val puzzleCompletionDAO: PuzzleCompletionDAO by lazy {
        database.puzzleCompletionDAO()
    }
}