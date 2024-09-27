package com.example.chessmate.ui.fragment

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chessmate.R
import com.example.chessmate.database.PuzzleCompletionRepository
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.AdvancedPuzzlesActivity
import com.example.chessmate.ui.activity.BeginnerPuzzlesActivity
import com.example.chessmate.ui.activity.IntermediatePuzzlesActivity
import com.example.chessmate.ui.viewmodel.PuzzlesViewModel
import com.example.chessmate.util.UserProfileManager

class PuzzlesFragment : Fragment() {
    private lateinit var beginnerPuzzlesButton: Button
    private lateinit var intermediatePuzzlesButton: Button
    private lateinit var advancedPuzzlesButton: Button
    private lateinit var solvedAllPuzzleCount: TextView
    private lateinit var solvedBeginnerPuzzleCount: TextView
    private lateinit var solvedIntermediatePuzzleCount: TextView
    private lateinit var solvedAdvancedPuzzleCount: TextView
    private var userProfile: UserProfile? = null
    private val userProfileManager = UserProfileManager.getInstance()

    companion object {
        fun newInstance() = PuzzlesFragment()
    }

    private lateinit var viewModel: PuzzlesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_puzzles, container, false)

        beginnerPuzzlesButton = view.findViewById(R.id.beginner_puzzles)
        intermediatePuzzlesButton = view.findViewById(R.id.intermediate_puzzles)
        advancedPuzzlesButton = view.findViewById(R.id.advanced_puzzles)

        solvedAllPuzzleCount = view.findViewById(R.id.solved_all_puzzles_count)
        solvedBeginnerPuzzleCount = view.findViewById(R.id.solved_beginner_puzzles_count)
        solvedIntermediatePuzzleCount = view.findViewById(R.id.solved_intermediate_puzzles_count)
        solvedAdvancedPuzzleCount = view.findViewById(R.id.solved_advanced_puzzles_count)

        beginnerPuzzlesButton.setOnClickListener { checkPuzzleButtonLock(beginnerPuzzlesButton, 1) }
        intermediatePuzzlesButton.setOnClickListener { checkPuzzleButtonLock(intermediatePuzzlesButton, 2) }
        advancedPuzzlesButton.setOnClickListener { checkPuzzleButtonLock(advancedPuzzlesButton, 3) }

        return view
    }

    override fun onResume() {
        super.onResume()
        userProfile = userProfileManager.getUserProfileLiveData().value
        updateLocks()

        lifecycleScope.launch {
            updatePuzzleCount()
        }
    }

    private fun checkPuzzleButtonLock(button: Button, id: Int) {
        if (userProfile == null) {
            loginToSolvePuzzles()
            return
        }

        if (userProfile!!.level == 0) {
            loginToSolvePuzzles()
        } else if (id <= userProfile!!.level) {
            animateButton(button, id)
        }
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
            1 -> openBeginnerPuzzles()
            2 -> openIntermediatePuzzles()
            3 -> openAdvancedPuzzles()
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

    private suspend fun updatePuzzleCount() {
        if (userProfile == null) return

        val puzzleCompletionRepository = PuzzleCompletionRepository(requireContext())
        val allPuzzleIds = puzzleCompletionRepository.getAllCompletedPuzzlesId(userProfile!!.userID)
        val beginnerPuzzleIds = puzzleCompletionRepository.getAllBeginnerPuzzlesId(userProfile!!.userID)
        val intermediatePuzzleIds = puzzleCompletionRepository.getAllIntermediatePuzzlesId(userProfile!!.userID)
        val advancedPuzzleIds = puzzleCompletionRepository.getAllAdvancedPuzzlesId(userProfile!!.userID)

        solvedAllPuzzleCount.text = getString(R.string.solved_all_puzzles_count, allPuzzleIds.size)
        solvedBeginnerPuzzleCount.text = getString(R.string.solved_beginner_puzzles_count, beginnerPuzzleIds.size)
        solvedIntermediatePuzzleCount.text = getString(R.string.solved_intermediate_puzzles_count, intermediatePuzzleIds.size)
        solvedAdvancedPuzzleCount.text = getString(R.string.solved_advanced_puzzles_count, advancedPuzzleIds.size)
    }

    private fun updateLocks() {
        addPuzzleButtonLocks()
        when (userProfile?.level) {
            1 -> updatePuzzleButtonLock(beginnerPuzzlesButton)
            2 -> {
                updatePuzzleButtonLock(beginnerPuzzlesButton)
                updatePuzzleButtonLock(intermediatePuzzlesButton)
            }
            3 -> {
                updatePuzzleButtonLock(beginnerPuzzlesButton)
                updatePuzzleButtonLock(intermediatePuzzlesButton)
                updatePuzzleButtonLock(advancedPuzzlesButton)

            }
        }
    }

    private fun updatePuzzleButtonLock(button: Button) {
        button.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
    }

    private fun addPuzzleButtonLocks() {
        val lockDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_lock_24)
        beginnerPuzzlesButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
        intermediatePuzzlesButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
        advancedPuzzlesButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
    }

    private fun loginToSolvePuzzles() {
        Toast.makeText(requireContext(), getString(R.string.login_to_access_solve_puzzles_message), Toast.LENGTH_LONG).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PuzzlesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}