package com.example.chessmate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.ChooseProfileAdapter
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.util.UserProfileManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ChooseProfile : AbsThemeActivity() {
    private lateinit var userProfileRepository: UserProfileRepository
    private val userProfileManager = UserProfileManager.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_profile)

        val toolbar = findViewById<Toolbar>(R.id.choose_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        userProfileRepository = UserProfileRepository(this)
    }

    override fun onStart() {
        super.onStart()

        val activity: ChooseProfile = this
        val recyclerView = findViewById<RecyclerView>(R.id.choose_profile_recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        lifecycleScope.launch {
            val userProfileList = userProfileRepository.getAllInactiveProfiles()
            val adapter = ChooseProfileAdapter(activity, userProfileList)
            recyclerView.adapter = adapter
        }
    }

    //important code because registerForActivityResult needs this
    override fun onBackPressed() {
        super.onBackPressed()
        val resultIntent = Intent()
        resultIntent.putExtra("profileChosen", true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    //this handles the profile changes in the database
    suspend fun changeProfiles(userID: Long){
        val deferred = CompletableDeferred<Boolean>()
        val currentUserID = userProfileManager.getUserProfileLiveData().value?.userID
        lifecycleScope.launch {
            coroutineScope {
                val deactivateResult = async {
                    try {
                        if (currentUserID != null) {
                            userProfileRepository.deactivateProfileByID(currentUserID) {errorMessage ->
                                showProfileChangeErrorMessage(errorMessage)
                            }
                        }
                        true
                    }catch (ex: Exception){
                        false
                    }
                }

                val activateResult = async {
                    userProfileRepository.activateProfileByID(userID) {errorMessage ->
                        showProfileChangeErrorMessage(errorMessage)
                    }
                }

                if (deactivateResult.await() && activateResult.await()) {
                    deferred.complete(true)
                } else {
                    deferred.complete(false)
                }
            }
        }
        finishTheActivityWithResult(deferred.await())
    }

    //this returns with a true or false. true if it could deactivate the current profile and activate the chosen profile
    private fun finishTheActivityWithResult(result: Boolean){
        if (result){
            val resultIntent = Intent()
            resultIntent.putExtra("profileChosen", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }else{
            val resultIntent = Intent()
            resultIntent.putExtra("profileChosen", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    //shows an error message to the user if something went wrong while changing profiles
    private fun showProfileChangeErrorMessage(errorMessage: String){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}