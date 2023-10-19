package com.example.chessmate.database

import com.example.chessmate.database.entity.UserProfile

data class ProfileResult(
    val userProfile: UserProfile?,
    val hasError: Boolean,
    val errorMessage: String?
)
