package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.OpeningAdapter
import com.example.chessmate.database.LessonCompletionRepository
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.JSONParser
import com.example.chessmate.util.OpeningRepo
import com.example.chessmate.util.UserProfileManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class OpeningsLessonsActivity : AbsThemeActivity() {
    private lateinit var whiteRecyclerView: RecyclerView
    private lateinit var blackRecyclerView: RecyclerView
    private lateinit var whiteAdapter: OpeningAdapter
    private lateinit var blackAdapter: OpeningAdapter
    private lateinit var toolbar : Toolbar
    private lateinit var lessonCompletionRepository: LessonCompletionRepository
    private var finishedOpenings: List<Int> = emptyList()
    private val userProfileManager = UserProfileManager.getInstance()
    private var userProfile: UserProfile? = null
    private var openingsRepo: List<OpeningRepo> = emptyList()
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_openings_lessons)

        lessonCompletionRepository = LessonCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        toolbar = findViewById(R.id.openings_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        whiteRecyclerView = findViewById(R.id.white_openings_recycler_view)
        whiteRecyclerView.layoutManager = LinearLayoutManager(this)
        blackRecyclerView = findViewById(R.id.black_opening_recycler_view)
        blackRecyclerView.layoutManager = LinearLayoutManager(this)

        openingsRepo = getOpenings()

        if (userProfile != null) {
            lifecycleScope.launch {
                finishedOpenings = preloadFinishedOpenings()
                val whiteOpenings = openingsRepo.take(5)
                val blackOpenings = openingsRepo.takeLast(5)
                whiteAdapter = OpeningAdapter(whiteOpenings, finishedOpenings, this@OpeningsLessonsActivity)
                blackAdapter = OpeningAdapter(blackOpenings, finishedOpenings, this@OpeningsLessonsActivity)
                whiteRecyclerView.adapter = whiteAdapter
                blackRecyclerView.adapter = blackAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::whiteAdapter.isInitialized && ::blackAdapter.isInitialized) {
            lifecycleScope.launch {
                finishedOpenings = preloadFinishedOpenings()
                whiteAdapter.updateFinishedOpening(finishedOpenings)
                blackAdapter.updateFinishedOpening(finishedOpenings)
            }
        }
    }

    private suspend fun preloadFinishedOpenings(): List<Int> {
        val result: MutableList<Int> = mutableListOf()

        openingsRepo.forEach { openingRepo ->
            val existingCompletion = lessonCompletionRepository.isOpeningFinished(userProfile!!.userID, openingRepo.lessonId)
            if (existingCompletion != null) {
                result.add(openingRepo.lessonId)
            }
        }
        return result
    }

    private fun getOpenings(): List<OpeningRepo> {
        val jsonString = jsonParser.loadJSONFromAsset(this, "opening_lessons.json")
        val openingsRepo: List<OpeningRepo> = Gson().fromJson(jsonString, object : TypeToken<List<OpeningRepo>>() {}.type)

        return openingsRepo.map { openingRepo ->
            openingRepo.copy(
                title = getStringFromResourceName(openingRepo.title)
            )
        }
    }

    private fun getStringFromResourceName(resourceName: String): String {
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) getString(resourceId) else getString(R.string.string_not_found)
    }
}