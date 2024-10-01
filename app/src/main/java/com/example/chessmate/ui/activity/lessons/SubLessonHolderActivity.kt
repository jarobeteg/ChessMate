package com.example.chessmate.ui.activity.lessons

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.adapter.SubLessonAdapter
import com.example.chessmate.ui.activity.AbsThemeActivity
import com.example.chessmate.util.JSONParser
import com.example.chessmate.util.Lesson
import com.example.chessmate.util.SubLesson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SubLessonHolderActivity : AbsThemeActivity() {
    private lateinit var subLessonRecyclerView: RecyclerView
    private lateinit var subLessonAdapter: SubLessonAdapter
    private lateinit var lessonTitle: TextView
    private lateinit var toolbar : Toolbar
    private var lesson: Lesson? = null
    private val jsonParser = JSONParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_lesson_holder)

        lesson = intent.getParcelableExtra("lesson")
        lessonTitle = findViewById(R.id.sub_lesson_holder_toolbar_title)

        toolbar = findViewById(R.id.sub_lesson_holder_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{onBackPressed()}

        subLessonRecyclerView = findViewById(R.id.sub_lesson_recycler_view)
        subLessonRecyclerView.layoutManager = LinearLayoutManager(this)

        val subLessons = getSubLessons()
        subLessonAdapter = SubLessonAdapter(subLessons, this)
        subLessonRecyclerView.adapter = subLessonAdapter

        updateTitle()
    }

    private fun updateTitle() {
        if (lesson == null) return
        lessonTitle.text = lesson!!.title
    }

    private fun getSubLessons(): List<SubLesson> {
        if (lesson == null) return emptyList()

        return lesson!!.subLessons.map { subLesson ->
            subLesson.copy(
                title = getStringFromResourceName(subLesson.title, subLesson.subLessonId)
            )
        }
    }

    private fun getStringFromResourceName(resourceName: String, id: Int): String {
        val resourceId = resources.getIdentifier(resourceName, "string", packageName)
        return if (resourceId != 0) getString(resourceId, id) else getString(R.string.string_not_found)
    }
}