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
    fun getAllUsers(): LiveData<List<UserProfile>>

    @Query("SELECT * FROM userprofile WHERE isActive = 1 LIMIT 1")
    fun getActiveProfile(): LiveData<UserProfile>

    @Query("UPDATE userprofile SET isActive = 0 WHERE isActive = 1")
    fun deactivateProfile()
}