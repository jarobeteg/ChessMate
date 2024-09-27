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
import com.example.chessmate.ui.activity.PuzzleLoaderActivity
import com.example.chessmate.util.Puzzle

class PuzzlesPagerAdapter(private val paginatedPuzzles: List<List<Puzzle>>, private val allPuzzles: List<Puzzle>, private var completedPuzzleIds: List<Int>, private val context: Context) : RecyclerView.Adapter<PuzzlesPagerAdapter.PuzzleViewHolder>() {
    class PuzzleViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val puzzleContainer: LinearLayout = itemView.findViewById(R.id.puzzle_container)

        fun bind(puzzles: List<Puzzle>, allPuzzles: List<Puzzle>, completedPuzzleIds: List<Int>) {
            puzzleContainer.removeAllViews()

            puzzles.forEach { puzzle ->
                val puzzleView = LayoutInflater.from(context).inflate(R.layout.item_puzzle, puzzleContainer, false)
                val puzzleDescription = puzzleView.findViewById<TextView>(R.id.puzzle_description)

                if (completedPuzzleIds.contains(puzzle.puzzleId)) {
                    val solvedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_solved_24)
                    puzzleDescription.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        solvedDrawable,
                        null
                    )
                }

                val result = context.getString(R.string.puzzle_text) + " " + puzzle.puzzleId.toString()
                puzzleDescription.text = result

                puzzleDescription.setTag(R.id.puzzle_description, puzzle)
                puzzleDescription.setOnClickListener {
                    val index = puzzle.puzzleId - 1
                    handlePuzzleClick(index, allPuzzles)
                }

                puzzleContainer.addView(puzzleView)
            }
        }

        private fun handlePuzzleClick(position: Int, puzzles: List<Puzzle>) {
            val intent = Intent(context, PuzzleLoaderActivity::class.java)
            intent.putExtra("puzzleList", ArrayList(puzzles))
            intent.putExtra("currentIndex", position)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_puzzle, parent, false)
        return PuzzleViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {
        val puzzlesForThisPage = paginatedPuzzles[position]
        holder.bind(puzzlesForThisPage, allPuzzles, completedPuzzleIds)
    }

    override fun getItemCount(): Int {
        return paginatedPuzzles.size
    }

    fun updateCompletedPuzzles(newCompletedPuzzlesIds: List<Int>) {
        this.completedPuzzleIds = newCompletedPuzzlesIds
        notifyDataSetChanged()
    }
}