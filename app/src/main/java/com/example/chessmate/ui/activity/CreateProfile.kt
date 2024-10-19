package com.example.chessmate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CompletableDeferred

class CreateProfile : AbsThemeActivity() {
    private var chosenLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val toolbar = findViewById<Toolbar>(R.id.create_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        val beginner = findViewById<TextView>(R.id.beginner_level)
        val intermediate = findViewById<TextView>(R.id.intermediate_level)
        val advanced = findViewById<TextView>(R.id.advanced_level)

        beginner.setOnClickListener {
            chosenLevel = 1
            updateTextViewBackgrounds(beginner, intermediate, advanced, 1)
        }

        intermediate.setOnClickListener {
            chosenLevel = 2
            updateTextViewBackgrounds(beginner, intermediate, advanced, 2)
        }

        advanced.setOnClickListener {
            chosenLevel = 3
            updateTextViewBackgrounds(beginner, intermediate, advanced, 3)
        }

        //after the checkmark has been clicked we first check if a username have been entered
        //if not then the app will show the correct error message
        val imageButton: ImageButton = findViewById(R.id.create_profile_checkmark)
        imageButton.setOnClickListener{
            if (!isUsernameEntered()) {
                showNoUsernameEntered()
            }else{
                //if username is entered the app calls the createNewUserProfile through a Coroutine
                //because the createNewUserProfile has suspend keyword in declaration because of the database queries and async code
                //this will return to the profile fragment with a true result if everything went correctly
                lifecycleScope.launch {
                    if (createNewUserProfile()) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("profileCreated", true)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }
    }

    //important code because registerForActivityResult needs this
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //this handles the new profile creation in the database and sets the current active profile inactive and the new profile active asynchronously
    private suspend fun createNewUserProfile(): Boolean{
        val username: String = findViewById<EditText>(R.id.new_profile_username).text.toString()
        val userRepo = UserProfileRepository(this)
        val newProfileLevel: Pair<Int, Int> = getNewProfileLevel()
        val profileRating = newProfileLevel.first
        val rating = newProfileLevel.second


        val resultUserProfile = UserProfile(
            username = username,
            rating = rating,
            level = profileRating,
            gamesPlayed = 0,
            puzzlesPlayed = 0,
            lessonsTaken = 0,
            isActive = true
        )

        val deferred = CompletableDeferred<Boolean>()

        //the async code part is important for the UI to function correctly otherwise the createNewUserProfile method returns earlier than the database query
        //and the UI needs to update because we insert a new active profile while we deactivate the previous one and the UI will need to update because
        //of the new active profile.
        lifecycleScope.launch {
            coroutineScope {
                val deactivateResult = async {
                    userRepo.deactivateUserProfile { errorMessage ->
                        showUnexpectedErrorMessage(errorMessage)
                    }
                }

                val insertResult = async {
                    try {
                        userRepo.insertUser(resultUserProfile) { errorMessage ->
                            showProfileInsertErrorMessage(errorMessage)
                        }
                        true
                    } catch (ex: Exception) {
                        false
                    }
                }

                if (deactivateResult.await() && insertResult.await()) {
                    deferred.complete(true)
                } else {
                    deferred.complete(false)
                }
            }
        }

        return deferred.await()
    }

    private fun getNewProfileLevel(): Pair<Int, Int> {
        val levelRating = chosenLevel
        val rating = when (levelRating) {
            1 -> 400
            2 -> 800
            3 -> 1200
            else -> 400
        }

        return Pair(levelRating, rating)
    }

    private fun isUsernameEntered(): Boolean{
        val usernameEditText: EditText = findViewById(R.id.new_profile_username)
        val username = usernameEditText.text.toString().trim()
        return username.isNotBlank()
    }

    private fun showNoUsernameEntered(){
        Toast.makeText(this, getString(R.string.missing_username), Toast.LENGTH_SHORT).show()
    }

    private fun showUnexpectedErrorMessage(errorMessage: String){
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showProfileInsertErrorMessage(errorMessage: String){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun updateTextViewBackgrounds(beginner: TextView, intermediate: TextView, advanced: TextView, selectedSide: Int) {
        beginner.setBackgroundResource(if (selectedSide == 1) R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
        intermediate.setBackgroundResource(if (selectedSide == 2) R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
        advanced.setBackgroundResource(if (selectedSide == 3) R.drawable.custom_rounded_textview else R.drawable.custom_rounded_unselected_textview)
    }
}