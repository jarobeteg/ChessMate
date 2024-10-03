package com.example.chessmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.util.OpeningRepo

class OpeningAdapter(private val openings: List<OpeningRepo>, private var finishedOpenings: List<Int>, private val context: Context) : RecyclerView.Adapter<OpeningAdapter.OpeningViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpeningViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_opening, parent, false)
        return OpeningViewHolder(view)
    }

    override fun onBindViewHolder(holder: OpeningViewHolder, position: Int) {
        val opening = openings[position]
        holder.bind(opening, position)

        val layoutParams = holder.openingTitle.layoutParams as LinearLayout.LayoutParams
        if (position == openings.size - 1) {
            layoutParams.bottomMargin = 16.dpToPx(holder.itemView.context)
        } else {
            layoutParams.bottomMargin = 0
        }
        holder.openingTitle.layoutParams = layoutParams
    }

    override fun getItemCount(): Int = openings.size

    fun updateFinishedOpening(newFinishedOpenings: List<Int>) {
        this.finishedOpenings = newFinishedOpenings
        notifyDataSetChanged()
    }

    inner class OpeningViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val openingTitle: TextView = itemView.findViewById(R.id.opening_title)

        fun bind(opening: OpeningRepo, position: Int) {
            openingTitle.text = opening.title
            openingTitle.setOnClickListener { handleOpeningClick(position) }

            if (finishedOpenings.contains(opening.lessonId)) {
                val solvedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_solved_24)
                openingTitle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    solvedDrawable,
                    null
                )
            }
        }

        private fun handleOpeningClick(position: Int) {

        }
    }
}