package com.example.chessmate.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.ui.viewmodel.ProfileViewModel
import com.example.chessmate.ui.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userProfileRepository: UserProfileRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userProfileRepository = UserProfileRepository(requireContext())

        val username = view.findViewById<TextView>(R.id.username)
        val level = view.findViewById<TextView>(R.id.level)
        val openingRating = view.findViewById<TextView>(R.id.openingRatingPoint)
        val midgameRating = view.findViewById<TextView>(R.id.midgameRatingPoint)
        val endgameRating = view.findViewById<TextView>(R.id.endgameRatingPoint)
        val gamesPlayed = view.findViewById<TextView>(R.id.gamesPlayedValue)
        val puzzlesPlayed = view.findViewById<TextView>(R.id.puzzlesPlayedValue)
        val lessonsTaken = view.findViewById<TextView>(R.id.lessonsTakenValue)
        val createUserProfile = view.findViewById<Button>(R.id.createUserProfile)

        viewModel = ViewModelProvider(this, ViewModelFactory(userProfileRepository))[ProfileViewModel::class.java]

        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
            showErrorToUser(errorMessage)
        })

        viewModel.userProfileLiveData.observe(viewLifecycleOwner, Observer { userProfile ->
            val levelText = getString(R.string.level) + userProfile.level
            username.text = userProfile.username
            level.text = levelText
            openingRating.text = userProfile.openingRating.toString()
            midgameRating.text = userProfile.midgameRating.toString()
            endgameRating.text = userProfile.endgameRating.toString()
            gamesPlayed.text = userProfile.gamesPlayed.toString()
            puzzlesPlayed.text = userProfile.puzzlesPlayed.toString()
            lessonsTaken.text = userProfile.lessonsTaken.toString()
        })

        viewModel.buttonClicked.observe(viewLifecycleOwner, Observer { clicked ->
            if (clicked){
                viewModel.onProfileCreationHandled()
            }
        })

        createUserProfile.setOnClickListener {
            viewModel.onCreateProfileClicked()
        }

        return view
    }

    private fun showErrorToUser(errorMessage: String){
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }
}