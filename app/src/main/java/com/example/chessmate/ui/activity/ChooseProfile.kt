package com.example.chessmate.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.ChooseProfileAdapter
import com.example.chessmate.database.UserProfileRepository
import kotlinx.coroutines.launch

class ChooseProfile : AbsThemeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_profile)

        val toolbar = findViewById<Toolbar>(R.id.choose_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onStart() {
        super.onStart()

        val recyclerView = findViewById<RecyclerView>(R.id.choose_profile_recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val userProfileRepository = UserProfileRepository(this)
        lifecycleScope.launch {
            val userProfileList = userProfileRepository.getAllInactiveProfiles()
            val adapter = ChooseProfileAdapter(userProfileList)
            recyclerView.adapter = adapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val resultIntent = Intent()
        resultIntent.putExtra("profileChosen", true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}