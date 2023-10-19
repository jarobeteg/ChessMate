package com.example.chessmate.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chessmate.database.ProfileResult
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.UserProfile

class ProfileViewModel(repository: UserProfileRepository): ViewModel() {
    //This is for retrieving the profile from the database with the help of ProfileResult data class which holds the necessary result of the query
    private val _profileResultLiveData = repository.findActiveProfile()
    val profileResultLiveData: LiveData<ProfileResult> get() = _profileResultLiveData

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

    //This is the Choose profile button. When the button is clicked it initiates the option to choose between profiles the user has created
    private val _initiateChooseProfile = MutableLiveData<Boolean>()
    val initiateChooseProfile: LiveData<Boolean>
        get() = _initiateChooseProfile

    fun initiateChooseProfile() {
        _initiateChooseProfile.value = true
    }

    fun onChooseProfileInitiated() {
        _initiateChooseProfile.value = false
    }

    //This is the Delete profile button. When the button is clicked it initiates the deletion of the current profile
    private val _initiateDeleteProfile = MutableLiveData<Boolean>()
    val initiateDeleteProfile: LiveData<Boolean>
        get() = _initiateDeleteProfile

    fun initiateDeleteProfile() {
        _initiateDeleteProfile.value = true
    }

    fun onDeleteProfileInitiated() {
        _initiateDeleteProfile.value = false
    }
}