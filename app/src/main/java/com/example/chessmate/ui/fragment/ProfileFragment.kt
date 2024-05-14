package com.example.chessmate.ui.fragment

import android.app.Activity
import android.app.AlertDialog
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
import androidx.lifecycle.lifecycleScope
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.ui.activity.ChooseProfile
import com.example.chessmate.ui.activity.CreateProfile
import com.example.chessmate.ui.viewmodel.ProfileViewModel
import com.example.chessmate.ui.viewmodel.ViewModelFactory
import com.example.chessmate.util.UserProfileManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userProfileRepository: UserProfileRepository
    private val userProfileManager = UserProfileManager.getInstance()

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
            viewModel.onProfileCreationInitiated()
        }

        //this waits for the ChooseProfile activity's result. if the returned value is true it means that the user has changed to a different profile
        val chooseProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            viewModel.onChooseProfileInitiated()
        }

        //this waits for the ChooseProfile activity's result after a profile has been deleted. if the returned value is true it means that the user has changed to a different profile
        val deleteProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            viewModel.onDeleteProfileInitiated()
        }

        //this observes the user profile data and displays it for the user
        //if no profile was found an error message is shown and a guest profile is loaded
        //because it's LiveData the UI should update automatically when a change occurs to the profile
        viewModel.profileResultLiveData.observe(viewLifecycleOwner, Observer { result ->
            if (result.hasError){
                result.errorMessage?.let { showErrorToUser(it) }
            }
            val userProfile = result.userProfile
            if (userProfile != null) {
                val levelText = getString(R.string.level) + userProfile.level
                username.text = userProfile.username
                level.text = levelText
                openingRating.text = userProfile.openingRating.toString()
                midgameRating.text = userProfile.midgameRating.toString()
                endgameRating.text = userProfile.endgameRating.toString()
                gamesPlayed.text = userProfile.gamesPlayed.toString()
                puzzlesPlayed.text = userProfile.puzzlesPlayed.toString()
                lessonsTaken.text = userProfile.lessonsTaken.toString()
                userProfileManager.setUserProfile(userProfile)
            }
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

        //this is called when the user click the deleteProfile button
        viewModel.initiateDeleteProfile.observe(viewLifecycleOwner, Observer { initiate ->
            if (initiate){
                val deleteProfileIntent = Intent(requireContext(), ChooseProfile::class.java)
                deleteProfileLauncher.launch(deleteProfileIntent)
            }
        })

        createUserProfile.setOnClickListener {
            createUserProfile.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    createUserProfile.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            viewModel.initiateProfileCreation()
        }

        changeProfile.setOnClickListener {
            viewModel.initiateChooseProfile()
        }

        deleteProfile.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setTitle(requireContext().getString(R.string.confirmation))
                setMessage(requireContext().getString(R.string.delete_profile_dialog_description))
                setPositiveButton(requireContext().getString(R.string.yes)) {dialog, which ->
                    lifecycleScope.launch {
                        if(deleteProfile()){
                            viewModel.initiateDeleteProfile()
                        }
                    }
                }
                setNegativeButton(requireContext().getString(R.string.no)) {dialog, which ->
                    dialog.dismiss()
                }

                create().show()
            }
        }

        return view
    }

    private suspend fun deleteProfile(): Boolean{
        val deferred = CompletableDeferred<Boolean>()
        val currentUserID = userProfileManager.getUserProfileLiveData().value?.userID
        lifecycleScope.launch {
            coroutineScope {
                val deleteResult = async {
                    try {
                        if (currentUserID != null) {
                            userProfileRepository.deleteProfileByID(currentUserID) {errorMessage ->
                                showProfileDeletionErrorMessage(errorMessage)
                            }
                        }
                        true
                    }catch (ex: Exception){
                        false
                    }
                }
                if (deleteResult.await()){
                    deferred.complete(true)
                }else{
                    deferred.complete(false)
                }
            }
        }
        return deferred.await()
    }

    //tells the user that no active profiles were found
    private fun showErrorToUser(errorMessage: String){
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showProfileDeletionErrorMessage(errorMessage: String){
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }
}