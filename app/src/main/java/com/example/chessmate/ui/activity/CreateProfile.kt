package com.example.chessmate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.chessmate.R

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
                val resultIntent = Intent()
                resultIntent.putExtra("profileCreated", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
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
}