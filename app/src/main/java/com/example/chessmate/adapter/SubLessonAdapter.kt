package com.example.chessmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.util.SubLesson

class SubLessonAdapter(private val subLessons: List<SubLesson>, private val context: Context) : RecyclerView.Adapter<SubLessonAdapter.SubLessonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubLessonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_lesson, parent, false)
        return SubLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubLessonViewHolder, position: Int) {
        val subLesson = subLessons[position]
        holder.bind(subLesson)
    }

    override fun getItemCount(): Int = subLessons.size

    inner class SubLessonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val subLessonTitle = itemView.findViewById<TextView>(R.id.sub_lesson_title)

        fun bind(subLesson: SubLesson) {
            subLessonTitle.text = subLesson.title
        }
    }
}