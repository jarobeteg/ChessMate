package com.example.chessmate.ui.fragment

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
import com.example.chessmate.database.entity.UserProfile
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
    private lateinit var username: TextView
    private lateinit var changeProfile: ImageButton
    private lateinit var deleteProfile: ImageButton
    private lateinit var level: TextView
    private lateinit var rating:TextView
    private lateinit var gamesPlayed: TextView
    private lateinit var puzzlesPlayed: TextView
    private lateinit var lessonsTaken: TextView
    private lateinit var createUserProfile: Button

    private lateinit var viewModel: ProfileViewModel
    private lateinit var userProfileRepository: UserProfileRepository
    private var userProfileManager = UserProfileManager.getInstance()
    private var userProfile: UserProfile? = null

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userProfileRepository = UserProfileRepository(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userProfileRepository))[ProfileViewModel::class.java]

        username = view.findViewById(R.id.username)
        changeProfile = view.findViewById(R.id.changeProfileButton)
        deleteProfile = view.findViewById(R.id.deleteProfileButton)
        level = view.findViewById(R.id.level)
        rating = view.findViewById(R.id.rating)
        gamesPlayed = view.findViewById(R.id.gamesPlayedValue)
        puzzlesPlayed = view.findViewById(R.id.puzzlesPlayedValue)
        lessonsTaken = view.findViewById(R.id.lessonsTakenValue)
        createUserProfile = view.findViewById(R.id.createUserProfile)

        //this waits for the CreateProfile activity's result. if the returned value is true it means that a profile has been created
        val profileCreationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            viewModel.onProfileCreationInitiated()
            loadUser()
        }

        //this waits for the ChooseProfile activity's result. if the returned value is true it means that the user has changed to a different profile
        val chooseProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            viewModel.onChooseProfileInitiated()
            loadUser()
        }

        //this waits for the ChooseProfile activity's result after a profile has been deleted. if the returned value is true it means that the user has changed to a different profile
        val deleteProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            viewModel.onDeleteProfileInitiated()
            loadUser()
        }

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

    override fun onResume() {
        super.onResume()
        loadUser()
    }

    private fun loadUserDataUI() {
        userProfile = userProfileManager.getUserProfileLiveData().value
        userProfile?.let {
            username.text = it.username
            level.text = getLevelText(it.level)
            rating.text = getRatingText(it.rating)
            gamesPlayed.text = it.gamesPlayed.toString()
            puzzlesPlayed.text = it.puzzlesPlayed.toString()
            lessonsTaken.text = it.lessonsTaken.toString()
        }
    }

    private fun loadUser() {
        viewModel.profileResultLiveData.observe(viewLifecycleOwner, Observer { result ->
            val newUserProfile = result.userProfile
            if (newUserProfile != null) {
                userProfileManager.setUserProfile(newUserProfile)
                loadUserDataUI()
            }
        })
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

    private fun getLevelText(level: Int): String {
        val result = when (level) {
            1 -> getString(R.string.beginner_level)
            2 -> getString(R.string.intermediate_level)
            3 -> getString(R.string.advanced_level)
            else -> getString(R.string.beginner_level)
        }

        return "$result: "
    }

    private fun getRatingText(rating: Int): String {
        return "$rating ${getString(R.string.rating_title).lowercase()}"
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