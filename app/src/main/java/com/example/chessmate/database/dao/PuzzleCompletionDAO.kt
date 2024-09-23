package com.example.chessmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chessmate.database.entity.PuzzleCompletion

@Dao
interface PuzzleCompletionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(puzzleCompletion: PuzzleCompletion)

    @Query("SELECT puzzleID FROM puzzlecompletions WHERE userID = :userID")
    suspend fun getAllCompletedPuzzleIdsForProfile(userID: Long): List<Int>

    @Query("SELECT puzzleID FROM puzzlecompletions WHERE userID = :userID AND difficulty = 0")
    suspend fun getAllCompletedBeginnerPuzzleIdsForProfile(userID: Long): List<Int>

    @Query("SELECT puzzleID FROM puzzlecompletions WHERE userID = :userID AND difficulty = 1")
    suspend fun getAllCompletedIntermediatePuzzleIdsForProfile(userID: Long): List<Int>

    @Query("SELECT puzzleID FROM puzzlecompletions WHERE userID = :userID AND difficulty = 2")
    suspend fun getAllCompletedAdvancedPuzzleIdsForProfile(userID: Long): List<Int>

    @Query("SELECT * FROM puzzlecompletions WHERE userID = :userID AND puzzleID = :puzzleID LIMIT 1")
    suspend fun isPuzzleSolved(userID: Long, puzzleID: Int): PuzzleCompletion?
}