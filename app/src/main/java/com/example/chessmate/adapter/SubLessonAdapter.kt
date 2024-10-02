package com.example.chessmate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.ui.activity.lessons.SubLessonLoaderActivity
import com.example.chessmate.util.LessonRepo
import com.example.chessmate.util.SubLesson

class SubLessonAdapter(private val lessonTitle: String, private val subLessons: List<SubLesson>,
                       private val lessonRepos: List<LessonRepo>, private var finishedSubLessons: List<Int>,
                       private val context: Context) : RecyclerView.Adapter<SubLessonAdapter.SubLessonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubLessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_lesson, parent, false)
        return SubLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubLessonViewHolder, position: Int) {
        val subLesson = subLessons[position]
        holder.bind(subLesson, position)
    }

    override fun getItemCount(): Int = subLessons.size

    fun updateFinishedSubLessons(newFinishedSubLessons: List<Int>) {
        this.finishedSubLessons = newFinishedSubLessons
        notifyDataSetChanged()
    }

    inner class SubLessonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val subLessonTitle = itemView.findViewById<TextView>(R.id.sub_lesson_title)

        fun bind(subLesson: SubLesson, position: Int) {
            subLessonTitle.text = subLesson.title
            subLessonTitle.setOnClickListener { handleSubLessonClick(position) }

            if (finishedSubLessons.contains(subLesson.subLessonId)) {
                val solvedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_solved_24)
                subLessonTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    solvedDrawable,
                    null
                )
            }
        }

        private fun handleSubLessonClick(position: Int) {
            val intent = Intent(context, SubLessonLoaderActivity::class.java)
            intent.putExtra("lessonRepos", ArrayList(lessonRepos))
            intent.putExtra("currentIndex", position)
            intent.putExtra("currentLesson", lessonTitle)
            context.startActivity(intent)
        }
    }
}