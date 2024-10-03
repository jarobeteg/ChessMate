package com.example.chessmate.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chessmate.database.entity.UserProfile

@Dao
interface UserProfileDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userProfile: UserProfile)

    @Query("SELECT * FROM userprofile WHERE userID = :userID")
    suspend fun getUserByID(userID: Long): UserProfile?

    @Query("SELECT * FROM userprofile")
    fun getAllUsers(): List<UserProfile>

    @Query("SELECT COUNT(*) FROM userprofile")
    fun countProfiles(): Int

    @Query("SELECT * FROM userprofile WHERE isActive = 0")
    fun getAllInactiveProfiles(): List<UserProfile>

    @Query("SELECT * FROM userprofile WHERE isActive = 1 LIMIT 1")
    fun getActiveProfile(): LiveData<UserProfile>

    @Query("UPDATE userprofile SET isActive = 0 WHERE isActive = 1")
    fun deactivateProfile()

    @Query("UPDATE userprofile SET isActive = 0 WHERE userID = :userID")
    suspend fun deactivateProfileByID(userID: Long)

    @Query("UPDATE userprofile SET isActive = 1 WHERE userID = :userID")
    suspend fun activateProfileByID(userID: Long)

    @Query("DELETE FROM userprofile WHERE userID = :userID")
    suspend fun deleteProfileByID(userID: Long)

    @Query("UPDATE userprofile SET puzzlesPlayed = puzzlesPlayed + 1 WHERE userID = :userID")
    suspend fun incrementPuzzlesPlayed(userID: Long)

    @Query("UPDATE userprofile SET gamesPlayed = gamesPlayed + 1 WHERE userID = :userID")
    suspend fun incrementGamesPlayed(userID: Long)

    @Query("UPDATE userprofile SET lessonsTaken = lessonsTaken + 1 WHERE userID = :userID")
    suspend fun incrementLessonsTaken(userID: Long)

    @Query("""
    UPDATE userprofile 
    SET 
        openingRating = CASE 
            WHEN openingRating + :openingIncrement < 400 THEN 400 
            WHEN openingRating + :openingIncrement > 1600 THEN 1600 
            ELSE openingRating + :openingIncrement 
        END,
        midgameRating = CASE 
            WHEN midgameRating + :midgameIncrement < 400 THEN 400 
            WHEN midgameRating + :midgameIncrement > 1600 THEN 1600 
            ELSE midgameRating + :midgameIncrement 
        END,
        endgameRating = CASE 
            WHEN endgameRating + :endgameIncrement < 400 THEN 400 
            WHEN endgameRating + :endgameIncrement > 1600 THEN 1600 
            ELSE endgameRating + :endgameIncrement 
        END
    WHERE userID = :userID
    """)
    suspend fun updateProfileRating(userID: Long, openingIncrement: Int, midgameIncrement: Int, endgameIncrement: Int)

    @Query("""
    UPDATE userprofile 
    SET 
        level = CASE
            WHEN openingRating >= 1200 AND midgameRating >= 1200 AND endgameRating >= 1200 THEN 3
            WHEN openingRating >= 800 AND midgameRating >= 800 AND endgameRating >= 800 THEN 2
            ELSE 1
        END
    WHERE userID = :userID
    """)
    suspend fun updateProfileLevel(userID: Long)
}