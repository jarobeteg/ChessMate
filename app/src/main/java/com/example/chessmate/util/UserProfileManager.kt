package com.example.chessmate.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chessmate.database.entity.UserProfile

class UserProfileManager {
    //created for future use because I think it could be a huge help later for some stuff but I might be wrong
    //if that happens i just delete this
    private val userProfileLiveData = MutableLiveData<UserProfile>()

    companion object {
        private var instance: UserProfileManager? = null

        fun getInstance(): UserProfileManager {
            if (instance == null) {
                instance = UserProfileManager()
            }
            return instance as UserProfileManager
        }
    }

    fun setUserProfile(profile: UserProfile) {
        //update the LiveData value so the UI should update as well because it's LiveData
        userProfileLiveData.value = profile

        //also I could update it in the database here if needed
        //probably need to so not just the UI updates but also the profile data gets stored
    }

    fun getUserProfileLiveData(): LiveData<UserProfile> {
        return userProfileLiveData
    }
}