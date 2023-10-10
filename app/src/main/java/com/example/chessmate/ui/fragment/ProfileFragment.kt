package com.example.chessmate.ui.fragment

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.ui.activity.ChooseProfile
import com.example.chessmate.ui.activity.CreateProfile
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
        viewModel = ViewModelProvider(this, ViewModelFactory(userProfileRepository))[ProfileViewModel::class.java]

        val username = view.findViewById<TextView>(R.id.username)
        val changeProfile = view.findViewById<ImageButton>(R.id.changeProfileButton)
        val deleteProfile = view.findViewById<ImageButton>(R.id.deleteProfileButton)
        val level = view.findViewById<TextView>(R.id.level)
        val openingRating = view.findViewById<TextView>(R.id.openingRatingPoint)
        val midgameRating = view.findViewById<TextView>(R.id.midgameRatingPoint)
        val endgameRating = view.findViewById<TextView>(R.id.endgameRatingPoint)
        val gamesPlayed = view.findViewById<TextView>(R.id.gamesPlayedValue)
        val puzzlesPlayed = view.findViewById<TextView>(R.id.puzzlesPlayedValue)
        val lessonsTaken = view.findViewById<TextView>(R.id.lessonsTakenValue)
        val createUserProfile = view.findViewById<Button>(R.id.createUserProfile)

        //this waits for the CreateProfile activity's result. if the returned value is true it means that a profile has been created
        val profileCreationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                val profileCreated = data?.getBooleanExtra("profileCreated", false) ?: false
                if (profileCreated) {
                    viewModel.onProfileCreationInitiated()
                }
            }
        }

        //this waits for the ChooseProfile activity's result. if the returned value is true it means that the user has changed to a different profile
        val chooseProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                val profileChosen = data?.getBooleanExtra("profileChosen", false) ?: false
                if (profileChosen) {
                    viewModel.onChooseProfileInitiated()
                }
            }
        }

        //this will call the showErrorToUser method and shows tells the user that no active profiles were found
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer { errorMessage ->
            showErrorToUser(errorMessage)
        })

        //this observes the user profile data and displays it for the user
        //because it's LiveData the UI should update automatically when a change occurs to the profile
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

        //this is called when the user clicks the createUserProfile button
        viewModel.initiateProfileCreation.observe(viewLifecycleOwner, Observer { initiate ->
            if (initiate){
                val createProfileIntent = Intent(requireContext(), CreateProfile::class.java)
                profileCreationLauncher.launch(createProfileIntent)
            }
        })

        //this is called when the user click the changeProfile button
        viewModel.initiateChooseProfile.observe(viewLifecycleOwner, Observer { initiate ->
            if (initiate){
                val chooseProfileIntent = Intent(requireContext(), ChooseProfile::class.java)
                chooseProfileLauncher.launch(chooseProfileIntent)
            }
        })

        createUserProfile.setOnClickListener {
            viewModel.initiateProfileCreation()
        }

        changeProfile.setOnClickListener {
            viewModel.initiateChooseProfile()
        }

        return view
    }

    //tells the user that no active profiles were found
    private fun showErrorToUser(errorMessage: String){
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }
}