package com.example.chessmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chessmate.database.UserProfileRepository

class ViewModelFactory(private val repository: UserProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            //attempt to create an instance of the specified ViewModel class using reflection
            val viewModelConstructor = modelClass.getConstructor(UserProfileRepository::class.java)
            return viewModelConstructor.newInstance(repository)
        } catch (ex: Exception) {
            throw IllegalArgumentException("Error creating ViewModel: ${ex.message}")
        }
    }
}
