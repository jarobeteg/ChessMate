package com.example.chessmate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.ui.activity.lessons.SubLessonHolderActivity
import com.example.chessmate.util.Lesson

class LessonAdapter(private val lessons: List<Lesson>, private var finishedLessons: List<Int>, private val context: Context) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.bind(lesson)

        val layoutParams = holder.lessonTitle.layoutParams as LinearLayout.LayoutParams
        if (position == lessons.size - 1) {
            layoutParams.bottomMargin = 16.dpToPx(holder.itemView.context)
        } else {
            layoutParams.bottomMargin = 0
        }
        holder.lessonTitle.layoutParams = layoutParams
    }

    override fun getItemCount(): Int = lessons.size

    fun updateFinishedLessons(newFinishedLessons: List<Int>) {
        this.finishedLessons = newFinishedLessons
        notifyDataSetChanged()
    }

    inner class LessonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val lessonTitle: TextView = itemView.findViewById(R.id.lesson_title)

        fun bind(lesson: Lesson) {
            lessonTitle.text = lesson.title
            lessonTitle.setOnClickListener { handleLessonClick(lesson) }

            if (finishedLessons.contains(lesson.lessonId)) {
                val solvedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_solved_24)
                lessonTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    solvedDrawable,
                    null
                )
            }
        }
    }

    private fun handleLessonClick(lesson: Lesson) {
        val intent = Intent(context, SubLessonHolderActivity::class.java)
        intent.putExtra("lesson", lesson)
        context.startActivity(intent)
    }
}

fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}