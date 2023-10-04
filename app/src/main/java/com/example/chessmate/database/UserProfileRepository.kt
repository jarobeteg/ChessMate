package com.example.chessmate.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.chessmate.database.dao.UserProfileDAO
import com.example.chessmate.database.entity.UserProfile

class UserProfileRepository(private val context: Context) {

    private val database: ChessMateDatabase by lazy {
        ChessMateDatabase.getDatabase(context)
    }

    private val userProfileDAO: UserProfileDAO by lazy {
        database.userProfileDAO()
    }

    fun getAllUsers(): LiveData<List<UserProfile>>{
        return userProfileDAO.getAllUsers()
    }
}