package com.example.chessmate.database

import android.content.Context
import com.example.chessmate.database.dao.LessonCompletionDAO
import com.example.chessmate.database.entity.LessonCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LessonCompletionRepository(private val context: Context) {

    private val database: ChessMateDatabase by lazy {
        ChessMateDatabase.getDatabase(context)
    }

    private val lessonCompletionDAO: LessonCompletionDAO by lazy {
        database.lessonCompletionDAO()
    }

    suspend fun insertLessonCompletion(lessonCompletion: LessonCompletion) {
        try {
            lessonCompletionDAO.insertCompletion(lessonCompletion)
        } catch (_: Exception) {}
    }

    suspend fun getAllTakenLessonsId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.getAllTakenLessonIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllChessBasicsLessonsId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.getAllTakenChessBasicsLessonIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPracticeLessonsId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.getAllTakenPracticeLessonIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllOpeningsLessonsId(userID: Long): List<Int> {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.getAllTakenOpeningsLessonIdsForProfile(userID)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun countCoordinateLessons(userID: Long): Int {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.countCoordinatesLessonForProfile(userID)
            }
        } catch (_: Exception) {
            0
        }
    }

    suspend fun isLessonSolved(userID: Long, lessonID: Int): LessonCompletion? {
        return try {
            withContext(Dispatchers.IO) {
                lessonCompletionDAO.isLessonTaken(userID, lessonID)
            }
        } catch (_: Exception) {
            null
        }
    }
}