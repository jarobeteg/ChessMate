package com.example.chessmate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.ui.activity.PuzzleLoaderActivity
import com.example.chessmate.util.Puzzle

class PuzzlesPagerAdapter(private val paginatedPuzzles: List<List<Puzzle>>, private val context: Context) : RecyclerView.Adapter<PuzzlesPagerAdapter.PuzzleViewHolder>() {
    class PuzzleViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val puzzleContainer: LinearLayout = itemView.findViewById(R.id.puzzle_container)

        fun bind(puzzles: List<Puzzle>) {
            puzzleContainer.removeAllViews()

            puzzles.forEach { puzzle ->
                val puzzleView = LayoutInflater.from(context).inflate(R.layout.item_puzzle, puzzleContainer, false)
                val puzzleDescription = puzzleView.findViewById<TextView>(R.id.puzzle_description)

                val result = context.getString(R.string.puzzle_text) + " " + puzzle.puzzleId.toString()
                puzzleDescription.text = result

                puzzleDescription.setTag(R.id.puzzle_description, puzzle)
                puzzleDescription.setOnClickListener {
                    val clickedPuzzle = it.getTag(R.id.puzzle_description) as Puzzle
                    handlePuzzleClick(clickedPuzzle)
                }

                puzzleContainer.addView(puzzleView)
            }
        }

        private fun handlePuzzleClick(puzzle: Puzzle) {
            val intent = Intent(context, PuzzleLoaderActivity::class.java)
            intent.putExtra("selectedPuzzle", puzzle)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_puzzle, parent, false)
        return PuzzleViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {
        val puzzlesForThisPage = paginatedPuzzles[position]
        holder.bind(puzzlesForThisPage)
    }

    override fun getItemCount(): Int {
        return paginatedPuzzles.size
    }
}