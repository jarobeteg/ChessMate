package com.example.chessmate.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chessmate.database.UserProfileRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UserProfileRepository): ViewModel() {
    //we check if the user has any profiles and if they don't have any we show a button and a text to create one
    private val _hasProfiles = MutableLiveData<Boolean>()
    val hasProfiles: LiveData<Boolean> get() = _hasProfiles

    fun checkProfiles() {
        viewModelScope.launch{
            val result = repository.hasProfiles()
            _hasProfiles.value = result
        }
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
        checkProfiles()
    }
}