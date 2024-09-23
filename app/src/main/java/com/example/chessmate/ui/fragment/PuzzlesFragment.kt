package com.example.chessmate.ui.fragment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.chessmate.R
import com.example.chessmate.ui.activity.AdvancedPuzzlesActivity
import com.example.chessmate.ui.activity.BeginnerPuzzlesActivity
import com.example.chessmate.ui.activity.IntermediatePuzzlesActivity
import com.example.chessmate.ui.viewmodel.PuzzlesViewModel
import com.example.chessmate.util.UserProfileManager

class PuzzlesFragment : Fragment() {

    companion object {
        fun newInstance() = PuzzlesFragment()
    }

    private lateinit var viewModel: PuzzlesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_puzzles, container, false)

        val beginnerPuzzlesButton = view.findViewById<Button>(R.id.beginner_puzzles)
        val intermediatePuzzlesButton = view.findViewById<Button>(R.id.intermediate_puzzles)
        val advancedPuzzlesButton = view.findViewById<Button>(R.id.advanced_puzzles)

        beginnerPuzzlesButton.setOnClickListener { animateButton(beginnerPuzzlesButton, 0) }
        intermediatePuzzlesButton.setOnClickListener { animateButton(intermediatePuzzlesButton, 1) }
        advancedPuzzlesButton.setOnClickListener { animateButton(advancedPuzzlesButton, 2) }

        println("user data: ${UserProfileManager.getInstance().getUserProfileLiveData().value?.username}")

        return view
    }

    private fun animateButton(button: Button, id: Int) {
        button.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        when (id) {
            0 -> openBeginnerPuzzles()
            1 -> openIntermediatePuzzles()
            2 -> openAdvancedPuzzles()
        }
    }

    private fun openBeginnerPuzzles(){
        val openBeginnerPuzzlesIntent = Intent(requireContext(), BeginnerPuzzlesActivity::class.java)
        startActivity(openBeginnerPuzzlesIntent)
    }
    private fun openIntermediatePuzzles(){
        val openIntermediatePuzzlesIntent = Intent(requireContext(), IntermediatePuzzlesActivity::class.java)
        startActivity(openIntermediatePuzzlesIntent)
    }
    private fun openAdvancedPuzzles(){
        val openAdvancedPuzzlesIntent = Intent(requireContext(), AdvancedPuzzlesActivity::class.java)
        startActivity(openAdvancedPuzzlesIntent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PuzzlesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}