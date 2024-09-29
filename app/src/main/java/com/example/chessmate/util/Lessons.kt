package com.example.chessmate.util

data class Section(
    val sectionId: Int,
    val title: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val lessonId: Int,
    val title: String,
    val contentFile: String,
    val subLessons: List<SubLesson>
)

data class SubLesson(
    val subLessonId: Int,
    val title: String,
)