package com.example.chessmate.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.UserProfile

class ProfileViewModel(repository: UserProfileRepository): ViewModel() {
    //This is for retrieving the profile from the database with error message if there is no active profiles in the database
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    val userProfileLiveData: LiveData<UserProfile> = repository.findActiveProfile { errorMessage ->
        _errorLiveData.value = errorMessage
    }

    //This is the Create new profile button. When the button is clicked it initiates the profile creation
    private val _initiateProfileCreation = MutableLiveData<Boolean>()
    val initiateProfileCreation: LiveData<Boolean>
        get() = _initiateProfileCreation

    fun initiateProfileCreation() {
        _initiateProfileCreation.value = true
    }

    fun onProfileCreationInitiated() {
        _initiateProfileCreation.value = false
    }
}