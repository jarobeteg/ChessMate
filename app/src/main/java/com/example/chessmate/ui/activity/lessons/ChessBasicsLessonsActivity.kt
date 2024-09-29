package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.SectionAdapter
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.JSONParser
import com.example.chessmate.util.Lesson
import com.example.chessmate.util.Section
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChessBasicsLessonsActivity : AbsThemeActivity() {
    private lateinit var sectionRecyclerView: RecyclerView
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var toolbar : Toolbar
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_basics_lessons)

        toolbar = findViewById(R.id.chess_basics_lesson_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        sectionRecyclerView = findViewById(R.id.section_recycler_view)
        sectionRecyclerView.layoutManager = LinearLayoutManager(this)

        val sections = getSections()
        sectionAdapter = SectionAdapter(sections)
        sectionRecyclerView.adapter = sectionAdapter
    }

    private fun getSections(): List<Section> {
        val jsonString = jsonParser.loadJSONFromAsset(this, "chess_basics_lesson.json")
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