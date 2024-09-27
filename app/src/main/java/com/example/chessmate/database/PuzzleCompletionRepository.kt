package com.example.chessmate.database

import android.content.Context
import com.example.chessmate.database.dao.PuzzleCompletionDAO
import com.example.chessmate.database.entity.PuzzleCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PuzzleCompletionRepository(private val context: Context) {

    private val database: ChessMateDatabase by lazy {
        ChessMateDatabase.getDatabase(context)
    }

    private val puzzleCompletionDAO: PuzzleCompletionDAO by lazy {
        database.puzzleCompletionDAO()
    }

    suspend fun insertPuzzleCompletion(puzzleCompletion: PuzzleCompletion) {
        try {
            puzzleCompletionDAO.insertCompletion(puzzleCompletion)
        } catch (_: Exception) {}
    }

    suspend fun getAllCompletedPuzzlesId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                puzzleCompletionDAO.getAllCompletedPuzzleIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllBeginnerPuzzlesId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                puzzleCompletionDAO.getAllCompletedBeginnerPuzzleIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllIntermediatePuzzlesId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                puzzleCompletionDAO.getAllCompletedIntermediatePuzzleIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllAdvancedPuzzlesId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                puzzleCompletionDAO.getAllCompletedAdvancedPuzzleIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun isPuzzleSolved(userID: Long, puzzleID: Int): PuzzleCompletion? {
        return try {
            withContext(Dispatchers.IO) {
                puzzleCompletionDAO.isPuzzleSolved(userID, puzzleID)
            }
        } catch (_: Exception) {
            null
        }
    }
}