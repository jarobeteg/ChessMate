package com.example.chessmate.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.chessmate.R
import com.example.chessmate.database.dao.UserProfileDAO
import com.example.chessmate.database.entity.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProfileRepository(private val context: Context) {

    private val database: ChessMateDatabase by lazy {
        ChessMateDatabase.getDatabase(context)
    }

    private val userProfileDAO: UserProfileDAO by lazy {
        database.userProfileDAO()
    }

    fun getAllUsers(): LiveData<List<UserProfile>>{
        return try {
            userProfileDAO.getAllUsers()
        } catch (ex: Exception) {
            MutableLiveData<List<UserProfile>>().apply { value = emptyList() }
        }
    }

    suspend fun insertUser(userProfile: UserProfile, onError: (String) -> Unit){
        try {
            userProfileDAO.insertUser(userProfile)
        } catch (ex: Exception){
            onError(context.getString(R.string.failed_to_insert_user))
        }
    }

    fun findActiveProfile(onError: (String) -> Unit): LiveData<UserProfile>{
        val activeProfileLiveData = userProfileDAO.getActiveProfile()

        val resultLiveData = MediatorLiveData<UserProfile>()

        resultLiveData.addSource(activeProfileLiveData) { activeProfile ->
            if (activeProfile != null) {
                resultLiveData.value = activeProfile
            } else {
                onError(context.getString(R.string.no_active_profile))
                resultLiveData.value = UserProfile(
                    username = "Guest",
                    openingRating = 0,
                    midgameRating = 0,
                    endgameRating = 0,
                    level = 0,
                    gamesPlayed = 0,
                    puzzlesPlayed = 0,
                    lessonsTaken = 0,
                    isActive = true
                )
            }
        }
        return resultLiveData
    }

    suspend fun deactivateUserProfile(onError: (String) -> Unit): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                userProfileDAO.deactivateProfile()
            }
            true
        } catch (ex: Exception) {
            onError(context.getString(R.string.unexpected_error))
            false
        }
    }
}