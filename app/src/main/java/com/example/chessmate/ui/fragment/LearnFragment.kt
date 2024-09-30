package com.example.chessmate.ui.fragment

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.LessonCompletionRepository
import com.example.chessmate.database.entity.UserProfile
import com.example.chessmate.ui.activity.lessons.ChessBasicsLessonsActivity
import com.example.chessmate.ui.activity.lessons.CoordinatesLessonActivity
import com.example.chessmate.ui.activity.lessons.OpeningsLessonsActivity
import com.example.chessmate.ui.activity.lessons.TacticalConceptsLessonsActivity
import com.example.chessmate.ui.viewmodel.LearnViewModel
import com.example.chessmate.util.UserProfileManager
import kotlinx.coroutines.launch

class LearnFragment : Fragment() {
    private lateinit var chessBasicsLessonsButton: Button
    private lateinit var tacticalConceptsLessonsButton: Button
    private lateinit var openingsLessonsButton: Button
    private lateinit var coordinatesLessonsButton: Button
    private lateinit var allTakenLessonsCount: TextView
    private lateinit var chessBasicsLessonsCount: TextView
    private lateinit var tacticalConceptsLessonsCount: TextView
    private lateinit var openingsLessonsCount: TextView
    private lateinit var coordinatesLessonsCount: TextView
    private var userProfile: UserProfile? = null
    private val userProfileManager = UserProfileManager.getInstance()

    companion object {
        fun newInstance() = LearnFragment()
    }

    private lateinit var viewModel: LearnViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_learn, container, false)

        chessBasicsLessonsButton = view.findViewById(R.id.chess_basics)
        tacticalConceptsLessonsButton = view.findViewById(R.id.tactical_concepts)
        openingsLessonsButton = view.findViewById(R.id.openings)
        coordinatesLessonsButton = view.findViewById(R.id.coordinates)

        allTakenLessonsCount = view.findViewById(R.id.all_taken_lessons_count)
        chessBasicsLessonsCount = view.findViewById(R.id.chess_basics_lesson_count)
        tacticalConceptsLessonsCount = view.findViewById(R.id.tactical_concepts_lesson_count)
        openingsLessonsCount = view.findViewById(R.id.openings_lesson_count)
        coordinatesLessonsCount = view.findViewById(R.id.coordinate_lesson_count)

        chessBasicsLessonsButton.setOnClickListener { checkLessonButtonLock(chessBasicsLessonsButton, 1) }
        tacticalConceptsLessonsButton.setOnClickListener { checkLessonButtonLock(tacticalConceptsLessonsButton, 2) }
        openingsLessonsButton.setOnClickListener { checkLessonButtonLock(openingsLessonsButton, 3) }
        coordinatesLessonsButton.setOnClickListener { checkLessonButtonLock(coordinatesLessonsButton, 4) }

        return view
    }

    override fun onResume() {
        super.onResume()
        userProfile = userProfileManager.getUserProfileLiveData().value
        updateLocks()

        lifecycleScope.launch {
            updateLessonCount()
        }
    }

    private fun checkLessonButtonLock(button: Button, id: Int) {
        if (userProfile == null) {
            loginToTakeLessons()
            return
        }

        if (userProfile!!.level == 0) {
            loginToTakeLessons()
        } else {
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
            1 -> openChessBasicsLessons()
            2 -> openTacticalConceptsLessons()
            3 -> openOpeningsLessons()
            4 -> openCoordinatesLessons()
        }
    }

    private fun openChessBasicsLessons(){
        val openChessBasicsLessonsIntent = Intent(requireContext(), ChessBasicsLessonsActivity::class.java)
        startActivity(openChessBasicsLessonsIntent)
    }

    private fun openTacticalConceptsLessons(){
        val openTacticalConceptsLessonsIntent = Intent(requireContext(), TacticalConceptsLessonsActivity::class.java)
        startActivity(openTacticalConceptsLessonsIntent)
    }

    private fun openOpeningsLessons(){
        val openOpeningsLessonsIntent = Intent(requireContext(), OpeningsLessonsActivity::class.java)
        startActivity(openOpeningsLessonsIntent)
    }

    private fun openCoordinatesLessons(){
        val openCoordinateLessonsIntent = Intent(requireContext(), CoordinatesLessonActivity::class.java)
        startActivity(openCoordinateLessonsIntent)
    }

    private suspend fun updateLessonCount() {
        if (userProfile == null) return

        val lessonCompletionRepository = LessonCompletionRepository(requireContext())
        val allLessonIds = lessonCompletionRepository.getAllTakenLessonsId(userProfile!!.userID)
        val chessBasicsLessonIds = lessonCompletionRepository.getAllChessBasicsLessonsId(userProfile!!.userID)
        val tacticalConceptsLessonIds = lessonCompletionRepository.getAllTacticalConceptsLessonsId(userProfile!!.userID)
        val openingsLessonIds = lessonCompletionRepository.getAllOpeningsLessonsId(userProfile!!.userID)
        val coordinatesCount = lessonCompletionRepository.countCoordinateLessons(userProfile!!.userID)

        allTakenLessonsCount.text = getString(R.string.all_lessons_taken_count, allLessonIds.size)
        chessBasicsLessonsCount.text = getString(R.string.chess_basics_lessons_taken_count, chessBasicsLessonIds.size)
        tacticalConceptsLessonsCount.text = getString(R.string.tactical_concepts_lessons_taken_count, tacticalConceptsLessonIds.size)
        openingsLessonsCount.text = getString(R.string.openings_lessons_taken_count, openingsLessonIds.size)
        coordinatesLessonsCount.text = getString(R.string.coordinates_lessons_taken_count, coordinatesCount)
    }

    private fun updateLocks() {
        addLessonsButtonLocks()
        if (userProfile?.level != 0) {
            updateLessonsButtonLock(chessBasicsLessonsButton)
            updateLessonsButtonLock(tacticalConceptsLessonsButton)
            updateLessonsButtonLock(openingsLessonsButton)
            updateLessonsButtonLock(coordinatesLessonsButton)
        }
    }

    private fun updateLessonsButtonLock(button: Button) {
        button.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
    }

    private fun addLessonsButtonLocks() {
        val lockDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_lock_24)
        chessBasicsLessonsButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
        tacticalConceptsLessonsButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
        openingsLessonsButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
        coordinatesLessonsButton.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            lockDrawable,
            null
        )
    }

    private fun loginToTakeLessons() {
        Toast.makeText(requireContext(), getString(R.string.login_to_access_lessons_message), Toast.LENGTH_LONG).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LearnViewModel::class.java)
        // TODO: Use the ViewModel
    }

}