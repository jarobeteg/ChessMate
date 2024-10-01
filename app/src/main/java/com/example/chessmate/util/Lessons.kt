package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Section(
    val sectionId: Int,
    val title: String,
    val lessons: List<Lesson>
)

@Parcelize
data class Lesson(
    val lessonId: Int,
    val title: String,
    val contentFile: String,
    val subLessons: List<SubLesson>
) : Parcelable

@Parcelize
data class SubLesson(
    val subLessonId: Int,
    val title: String,
) : Parcelable