package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.SectionAdapter
import com.example.chessmate.database.LessonCompletionRepository
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.JSONParser
import com.example.chessmate.util.Section
import com.example.chessmate.util.UserProfileManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class TacticalConceptsLessonsActivity : AbsThemeActivity() {
    private lateinit var sectionRecyclerView: RecyclerView
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var toolbar : Toolbar
    private lateinit var lessonCompletionRepository: LessonCompletionRepository
    private var finishedLessons: List<Int> = emptyList()
    private val userProfileManager = UserProfileManager.getInstance()
    private var userProfile: UserProfile? = null
    private var sections: List<Section> = emptyList()
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tactical_concepts_lessons)

        lessonCompletionRepository = LessonCompletionRepository(this)
        userProfile = userProfileManager.getUserProfileLiveData().value

        toolbar = findViewById(R.id.tactical_concepts_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        sectionRecyclerView = findViewById(R.id.section_recycler_view)
        sectionRecyclerView.layoutManager = LinearLayoutManager(this)

        sections = getSections()

        if (userProfile != null) {
            lifecycleScope.launch {
                finishedLessons = preloadFinishedLessons()
                sectionAdapter = SectionAdapter(sections, finishedLessons, this@TacticalConceptsLessonsActivity)
                sectionRecyclerView.adapter = sectionAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::sectionAdapter.isInitialized) {
            lifecycleScope.launch {
                finishedLessons = preloadFinishedLessons()
                sectionAdapter.updateFinishedLessons(finishedLessons)
            }
        }
    }

    private suspend fun preloadFinishedLessons(): List<Int> {
        val subLessonIds: MutableList<Int> = mutableListOf()
        val result: MutableList<Int> = mutableListOf()

        sections.forEach { section ->
            section.lessons.forEach { lesson ->
                subLessonIds.clear()
                lesson.subLessons.forEach{ subLesson ->
                    subLessonIds.add(subLesson.subLessonId)
                }
                val isLessonFinished = lessonCompletionRepository.isLessonFinished(userProfile!!.userID, lesson.lessonId, subLessonIds)
                if (isLessonFinished) {
                    result.add(lesson.lessonId)
                }
            }
        }

        return result
    }

    private fun getSections(): List<Section> {
        val jsonString = jsonParser.loadJSONFromAsset(this, "tactical_concepts_lesson.json")
        val sections: List<Section> = Gson().fromJson(jsonString, object : TypeToken<List<Section>>() {}.type)

        return sections.map { section ->
            section.copy(
                title = getStringFromResourceName(section.title),
                lessons = section.lessons.map { lesson ->
                    lesson.copy(
                        title = getStringFromResourceName(lesson.title)
                    )
                }
            )
        }
    }

    private fun getStringFromResourceName(resourceName: String): String {
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) getString(resourceId) else getString(R.string.string_not_found)
    }
}