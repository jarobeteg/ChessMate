package com.example.chessmate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.database.entity.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlinx.coroutines.CompletableDeferred

class CreateProfile : AbsThemeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val toolbar = findViewById<Toolbar>(R.id.create_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        //after the checkmark has been clicked we first check if a username have been answered and did the user have answered all the questions
        //if not then the app will show the correct error message to the user explaining that they missed something
        val imageButton: ImageButton = findViewById(R.id.create_profile_checkmark)
        imageButton.setOnClickListener{
            if (!isUsernameEntered() || !areQuestionsAnswered()) {
                if (!isUsernameEntered() && !areQuestionsAnswered()) {
                    showBothErrorMessages()
                }
                else if (!isUsernameEntered()) {
                    showNoUsernameEntered()
                }
                else if (!areQuestionsAnswered()) {
                    showNotAllQuestionsAnswered()
                }
            }else{
                //if both username and all questions have been answered the app calls the createNewUserProfile through a Coroutine
                //because the createNewUserProfile has suspend keyword in declaration because of the database queries and async code
                //this will return to the profile fragment with a true result if everything went correctly
                lifecycleScope.launch {
                    if (createNewUserProfile()) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("profileCreated", true)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        val resultIntent = Intent()
                        resultIntent.putExtra("profileCreated", false)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    //this handles the new profile creation in the database and sets the current active profile inactive and the new profile active asynchronously
    private suspend fun createNewUserProfile(): Boolean{
        val username: String = findViewById<EditText>(R.id.new_profile_username).text.toString()
        val userRepo = UserProfileRepository(this)
        val profileLevel: Int = getNewProfileLevel()

        val resultRating = when (profileLevel){
            1 -> 350
            2 -> 650
            3 -> 950
            4 -> 1250
            5 -> 1550
            6 -> 1850
            7 -> 2150
            8 -> 2450
            9 -> 2750
            10 -> 3000
            else -> 200
        }

        val resultUserProfile = UserProfile(
            username = username,
            openingRating = resultRating,
            midgameRating = resultRating,
            endgameRating = resultRating,
            level = profileLevel,
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

    //this evaluates the answers and returns the corresponding level for the profile
    private fun getNewProfileLevel(): Int {
        var levelPoints = 0

        val radioQuestion1: RadioGroup = findViewById(R.id.question1)
        val radioQuestion2: RadioGroup = findViewById(R.id.question2)
        val radioQuestion3: RadioGroup = findViewById(R.id.question3)
        val radioQuestion4: RadioGroup = findViewById(R.id.question4)
        val radioQuestion5: RadioGroup = findViewById(R.id.question5)
        val radioQuestion6: RadioGroup = findViewById(R.id.question6)
        val radioQuestion7: RadioGroup = findViewById(R.id.question7)

        val radioAnswer1 = radioQuestion1.checkedRadioButtonId
        val radioAnswer2 = radioQuestion2.checkedRadioButtonId
        val radioAnswer3 = radioQuestion3.checkedRadioButtonId
        val radioAnswer4 = radioQuestion4.checkedRadioButtonId
        val radioAnswer5 = radioQuestion5.checkedRadioButtonId
        val radioAnswer6 = radioQuestion6.checkedRadioButtonId
        val radioAnswer7 = radioQuestion7.checkedRadioButtonId

        levelPoints += when (radioAnswer1) {
            R.id.answer1A -> 6
            R.id.answer1B -> 4
            R.id.answer1C -> 1
            else -> 0
        }

        levelPoints += when (radioAnswer2) {
            R.id.answer2A -> 5
            R.id.answer2B -> 3
            R.id.answer2C -> 1
            else -> 0
        }

        levelPoints += when (radioAnswer3) {
            R.id.answer3A -> 8
            R.id.answer3B -> 5
            R.id.answer3C -> 2
            else -> 0
        }

        levelPoints += when (radioAnswer4) {
            R.id.answer4A -> 9
            R.id.answer4B -> 7
            R.id.answer4C -> 4
            else -> 0
        }

        levelPoints += when (radioAnswer5) {
            R.id.answer5A -> 8
            R.id.answer5B -> 5
            R.id.answer5C -> 2
            else -> 0
        }

        levelPoints += when (radioAnswer6) {
            R.id.answer6A -> 7
            R.id.answer6B -> 5
            R.id.answer6C -> 3
            else -> 0
        }

        levelPoints += when (radioAnswer7) {
            R.id.answer7A -> 9
            R.id.answer7B -> 6
            R.id.answer7C -> 3
            else -> 0
        }

        return (levelPoints.toDouble() / 7).roundToInt()
    }

    //this check if the user has answered to all questions and if they it the method returns true
    private fun areQuestionsAnswered(): Boolean{
        val radioQuestion1: RadioGroup = findViewById(R.id.question1)
        val radioQuestion2: RadioGroup = findViewById(R.id.question2)
        val radioQuestion3: RadioGroup = findViewById(R.id.question3)
        val radioQuestion4: RadioGroup = findViewById(R.id.question4)
        val radioQuestion5: RadioGroup = findViewById(R.id.question5)
        val radioQuestion6: RadioGroup = findViewById(R.id.question6)
        val radioQuestion7: RadioGroup = findViewById(R.id.question7)

        val radioGroups: List<RadioGroup> = listOf(radioQuestion1, radioQuestion2, radioQuestion3, radioQuestion4, radioQuestion5, radioQuestion6, radioQuestion7)

        for (group in radioGroups){
            if (group.checkedRadioButtonId == -1){
                return false
            }
        }
        return true
    }

    private fun isUsernameEntered(): Boolean{
        val usernameEditText: EditText = findViewById(R.id.new_profile_username)
        val username = usernameEditText.text.toString().trim()
        return username.isNotBlank()
    }

    private fun showNotAllQuestionsAnswered(){
        Toast.makeText(this, getString(R.string.not_all_questions_answered), Toast.LENGTH_SHORT).show()
    }

    private fun showNoUsernameEntered(){
        Toast.makeText(this, getString(R.string.missing_username), Toast.LENGTH_SHORT).show()
    }

    private fun showBothErrorMessages(){
        Toast.makeText(this, getString(R.string.no_username_and_answers), Toast.LENGTH_LONG).show()
    }

    private fun showUnexpectedErrorMessage(errorMessage: String){
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showProfileInsertErrorMessage(errorMessage: String){
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}