package com.example.chessmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.util.Section

class SectionAdapter(private val sections: List<Section>, private var finishedLessons: List<Int>, private val context: Context) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sections.size

    fun updateFinishedLessons(newFinishedLessons: List<Int>) {
        this.finishedLessons = newFinishedLessons
        notifyDataSetChanged()
    }

    inner class SectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val sectionTitle: TextView = itemView.findViewById(R.id.section_title)
        private val lessonsRecyclerView: RecyclerView = itemView.findViewById(R.id.lessons_recycler_view)

        private var lessonAdapter: LessonAdapter? = null

        fun bind(section: Section) {
            sectionTitle.text = section.title
            lessonsRecyclerView.layoutManager = LinearLayoutManager(context)
            lessonsRecyclerView.adapter = LessonAdapter(section.lessons, finishedLessons, context)

            if (lessonAdapter == null) {
                lessonAdapter = LessonAdapter(section.lessons, finishedLessons, context)
                lessonsRecyclerView.adapter = lessonAdapter
            } else {
                lessonAdapter?.updateFinishedLessons(finishedLessons)
            }
        }
    }
}