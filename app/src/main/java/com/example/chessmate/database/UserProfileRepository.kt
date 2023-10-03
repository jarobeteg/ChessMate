package com.example.chessmate.database

import androidx.lifecycle.LiveData
import com.example.chessmate.database.dao.UserProfileDAO
import com.example.chessmate.database.entity.UserProfile

class UserProfileRepository(private val userProfileDAO: UserProfileDAO) {

    fun getAllUsers(): LiveData<List<UserProfile>>{
        return userProfileDAO.getAllUsers()
    }
}