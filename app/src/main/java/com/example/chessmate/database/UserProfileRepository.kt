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

    //this returns all the profiles the user have created. the withContext(Dispatchers.IO) is critical here because
    //it ensures that the database query doesn't block the main thread or any other critical thread to prevent UI freezes
    suspend fun getAllUsers(): List<UserProfile>{
        return try {
            withContext(Dispatchers.IO) {
                userProfileDAO.getAllUsers()
            }
        } catch (ex: Exception) {
           emptyList()
        }
    }

    suspend fun hasProfiles(): Boolean {
        var count: Int
        withContext(Dispatchers.IO){
            count = userProfileDAO.countProfiles()
        }
        return count > 0
    }

    //this returns all the inactive profiles the user have created. the withContext(Dispatchers.IO) is critical here because
    //it ensures that the database query doesn't block the main thread or any other critical thread to prevent UI freezes
    suspend fun getAllInactiveProfiles(): List<UserProfile>{
        return try {
            withContext(Dispatchers.IO) {
                userProfileDAO.getAllInactiveProfiles()
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    //this handles the insertion of a new profile into the database
    suspend fun insertUser(userProfile: UserProfile, onError: (String) -> Unit){
        try {
            userProfileDAO.insertUser(userProfile)
        } catch (ex: Exception){
            onError(context.getString(R.string.failed_to_insert_user))
        }
    }

    //this finds the only active profile in the database and displays it in the profile fragment
    //if there is no active profiles a guest profile is loaded
    fun findActiveProfile(): LiveData<ProfileResult> {
        val activeProfileLiveData = userProfileDAO.getActiveProfile()

        val resultLiveData = MediatorLiveData<ProfileResult>()

        resultLiveData.addSource(activeProfileLiveData) { activeProfile ->
            val hasActiveProfile = activeProfile != null

            if (hasActiveProfile) {
                resultLiveData.value = ProfileResult(activeProfile, false, null)
            } else {
                val defaultProfile = UserProfile(
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
                resultLiveData.value = ProfileResult(defaultProfile, true, context.getString(R.string.no_active_profile))
            }
        }

        return resultLiveData
    }

    //this handles the current active profile's deactivation. the withContext(Dispatchers.IO) is critical here because
    //it ensures that the database query doesn't block the main thread or any other critical thread to prevent UI freezes
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

    //this handles the current active profile's deactivation by ID. the withContext(Dispatchers.IO) is critical here because
    //it ensures that the database query doesn't block the main thread or any other critical thread to prevent UI freezes
    suspend fun deactivateProfileByID(userID: Long, onError: (String) -> Unit): Boolean{
        return try{
            withContext(Dispatchers.IO){
                userProfileDAO.deactivateProfileByID(userID)
            }
            true
        }catch (ex: Exception){
            onError(context.getString(R.string.profile_change_error))
            false
        }
    }

    //this handles the selected profile activation by id. the withContext(Dispatchers.IO) is critical here because
    //it ensures that the database query doesn't block the main thread or any other critical thread to prevent UI freezes
    suspend fun activateProfileByID(userID: Long, onError: (String) -> Unit): Boolean{
       return try {
            withContext(Dispatchers.IO){
                userProfileDAO.activateProfileByID(userID)
            }
           true
        }catch (ex: Exception){
            onError(context.getString(R.string.profile_change_error))
           false
        }
    }

    suspend fun deleteProfileByID(userID: Long, onError: (String) -> Unit): Boolean{
        return try {
            withContext(Dispatchers.IO){
                userProfileDAO.deleteProfileByID(userID)
            }
            true
        }catch (ex: Exception){
            onError(context.getString(R.string.profile_delete_error))
            false
        }
    }
}