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

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID")
    suspend fun getAllTakenLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type = 1")
    suspend fun getAllTakenChessBasicsLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID AND type = 2")
    suspend fun getAllTakenPracticeLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT lessonID FROM lessoncompletions WHERE userID = :userID and type = 3")
    suspend fun getAllTakenOpeningsLessonIdsForProfile(userID: Long): List<Int>

    @Query("SELECT * FROM lessoncompletions WHERE userID = :userID and lessonID = :lessonID LIMIT 1")
    suspend fun isLessonTaken(userID: Long, lessonID: Int): LessonCompletion?
}