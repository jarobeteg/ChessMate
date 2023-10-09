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

class CreateProfile : AbsThemeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val toolbar = findViewById<Toolbar>(R.id.create_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

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
                if(createNewUserProfile()) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("profileCreated", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }else{
                    val resultIntent = Intent()
                    resultIntent.putExtra("profileCreated", false)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    private fun createNewUserProfile(): Boolean{
        val username: String = findViewById<EditText>(R.id.new_profile_username).text.toString()
        val userRepo = UserProfileRepository(this)
        val profileLevel: Int = getNewProfileLevel()
        var result = false

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
                    result = true
                }
            }
        }

        return result
    }

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