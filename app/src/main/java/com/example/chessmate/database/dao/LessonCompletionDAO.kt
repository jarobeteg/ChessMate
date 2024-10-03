package com.example.chessmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chessmate.database.entity.LessonCompletion

@Dao
interface LessonCompletionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(lessonCompletion: LessonCompletion)

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type IN (1, 2, 3)")
    suspend fun getAllTakenLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type = 1")
    suspend fun getAllTakenChessBasicsLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type = 2")
    suspend fun getAllTakenTacticalConceptsLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type = 3")
    suspend fun getAllTakenOpeningsLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT COUNT(*) FROM lessoncompletions WHERE userID = :userID AND type = 0")
    suspend fun countCoordinatesLessonForProfile(userID: Long): Int

    @Query("SELECT * FROM lessoncompletions WHERE userID = :userID AND lessonID = :lessonID LIMIT 1")
    suspend fun isOpeningTaken(userID: Long, lessonID: Int): LessonCompletion?

    @Query("SELECT * FROM lessoncompletions WHERE userID = :userID AND lessonID = :lessonID AND subLessonID = :subLessonID LIMIT 1")
    suspend fun isSubLessonTaken(userID: Long, lessonID: Int, subLessonID: Int): LessonCompletion?
}