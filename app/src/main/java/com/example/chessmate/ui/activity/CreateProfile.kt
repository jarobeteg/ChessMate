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
import kotlinx.coroutines.CompletableDeferred
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
        val newProfileLevel: IntArray = getNewProfileLevel()
        val profileRating = newProfileLevel[0]
        val openingRating = ((newProfileLevel[1] + profileRating).toDouble() / 6).roundToInt()
        val midgameRating = ((newProfileLevel[2] + profileRating).toDouble() / 6).roundToInt()
        val endgameRating = ((newProfileLevel[3] + profileRating).toDouble() / 6).roundToInt()

        val resultOpeningRating = when (openingRating){
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

        val resultMidgameRating = when (midgameRating){
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

        val resultEndgameRating = when (endgameRating){
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

        val resultProfileLevel = when (((resultOpeningRating + resultMidgameRating + resultEndgameRating).toDouble() / 3).roundToInt()){
            in 200..500 -> 1
            in 501..800 -> 2
            in 801..1100 -> 3
            in 1101..1400 -> 4
            in 1401..1700 -> 5
            in 1701..2000 -> 6
            in 2001..2300 -> 7
            in 2301..2600 -> 8
            in 2601..2900 -> 9
            else -> if (resultOpeningRating + resultMidgameRating + resultEndgameRating >= 2901) 10 else 0
        }


        val resultUserProfile = UserProfile(
            username = username,
            openingRating = resultOpeningRating,
            midgameRating = resultMidgameRating,
            endgameRating = resultEndgameRating,
            level = resultProfileLevel,
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
    private fun getNewProfileLevel(): IntArray {
        var levelRating = 0
        var openingRating = 0
        var midgameRating = 0
        var endgameRating = 0

        val radioQuestion1: RadioGroup = findViewById(R.id.question1)
        val radioQuestion2: RadioGroup = findViewById(R.id.question2)
        val radioQuestion3: RadioGroup = findViewById(R.id.question3)
        val radioQuestion4: RadioGroup = findViewById(R.id.question4)
        val radioQuestion5: RadioGroup = findViewById(R.id.question5)
        val radioQuestion6: RadioGroup = findViewById(R.id.question6)
        val radioQuestion7: RadioGroup = findViewById(R.id.question7)
        val radioQuestion8: RadioGroup = findViewById(R.id.question8)
        val radioQuestion9: RadioGroup = findViewById(R.id.question9)
        val radioQuestion10: RadioGroup = findViewById(R.id.question10)
        val radioQuestion11: RadioGroup = findViewById(R.id.question11)
        val radioQuestion12: RadioGroup = findViewById(R.id.question12)

        val radioAnswer1 = radioQuestion1.checkedRadioButtonId
        val radioAnswer2 = radioQuestion2.checkedRadioButtonId
        val radioAnswer3 = radioQuestion3.checkedRadioButtonId
        val radioAnswer4 = radioQuestion4.checkedRadioButtonId
        val radioAnswer5 = radioQuestion5.checkedRadioButtonId
        val radioAnswer6 = radioQuestion6.checkedRadioButtonId
        val radioAnswer7 = radioQuestion7.checkedRadioButtonId
        val radioAnswer8 = radioQuestion8.checkedRadioButtonId
        val radioAnswer9 = radioQuestion9.checkedRadioButtonId
        val radioAnswer10 = radioQuestion10.checkedRadioButtonId
        val radioAnswer11 = radioQuestion11.checkedRadioButtonId
        val radioAnswer12 = radioQuestion12.checkedRadioButtonId

        //general level rating
        levelRating += when (radioAnswer1) {
            R.id.answer1A -> 6
            R.id.answer1B -> 4
            R.id.answer1C -> 1
            else -> 0
        }

        levelRating += when (radioAnswer2) {
            R.id.answer2A -> 5
            R.id.answer2B -> 3
            R.id.answer2C -> 1
            else -> 0
        }

        levelRating += when (radioAnswer3) {
            R.id.answer3A -> 7
            R.id.answer3B -> 4
            R.id.answer3C -> 2
            else -> 0
        }

        //opening rating
        openingRating += when (radioAnswer4) {
            R.id.answer4A -> 8
            R.id.answer4B -> 5
            R.id.answer4C -> 2
            else -> 0
        }

        openingRating += when (radioAnswer5) {
            R.id.answer5A -> 7
            R.id.answer5B -> 4
            R.id.answer5C -> 2
            else -> 0
        }

        openingRating += when (radioAnswer6) {
            R.id.answer6A -> 8
            R.id.answer6B -> 4
            R.id.answer6C -> 2
            else -> 0
        }

        //midgame rating
        midgameRating += when (radioAnswer7) {
            R.id.answer7A -> 7
            R.id.answer7B -> 5
            R.id.answer7C -> 2
            else -> 0
        }

        midgameRating += when (radioAnswer8) {
            R.id.answer8A -> 8
            R.id.answer8B -> 5
            R.id.answer8C -> 2
            else -> 0
        }

        midgameRating += when (radioAnswer9) {
            R.id.answer9A -> 8
            R.id.answer9B -> 5
            R.id.answer9C -> 2
            else -> 0
        }

        //endgame rating
        endgameRating += when (radioAnswer10) {
            R.id.answer10A -> 8
            R.id.answer10B -> 5
            R.id.answer10C -> 2
            else -> 0
        }

        endgameRating += when (radioAnswer11) {
            R.id.answer11A -> 9
            R.id.answer11B -> 6
            R.id.answer11C -> 3
            else -> 0
        }

        endgameRating += when (radioAnswer12) {
            R.id.answer12A -> 9
            R.id.answer12B -> 6
            R.id.answer12C -> 3
            else -> 0
        }

        return intArrayOf(levelRating, openingRating, midgameRating, endgameRating)
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
        val radioQuestion8: RadioGroup = findViewById(R.id.question8)
        val radioQuestion9: RadioGroup = findViewById(R.id.question9)
        val radioQuestion10: RadioGroup = findViewById(R.id.question10)
        val radioQuestion11: RadioGroup = findViewById(R.id.question11)
        val radioQuestion12: RadioGroup = findViewById(R.id.question12)

        val radioGroups: List<RadioGroup> = listOf(radioQuestion1, radioQuestion2, radioQuestion3, radioQuestion4, radioQuestion5, radioQuestion6,
            radioQuestion7, radioQuestion8, radioQuestion9, radioQuestion10, radioQuestion11, radioQuestion12)

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