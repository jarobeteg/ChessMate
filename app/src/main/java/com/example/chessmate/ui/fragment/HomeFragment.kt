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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.example.chessmate.R
import com.example.chessmate.database.UserProfileRepository
import com.example.chessmate.ui.activity.CreateProfile
import com.example.chessmate.ui.activity.PlayActivity
import com.example.chessmate.ui.viewmodel.HomeViewModel
import com.example.chessmate.ui.viewmodel.ViewModelFactory
import com.example.chessmate.util.UserProfileManager

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var userProfileRepository: UserProfileRepository
    private val userProfileManager = UserProfileManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        userProfileRepository = UserProfileRepository(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userProfileRepository))[HomeViewModel::class.java]

        //the welcome to ChessMate text is only shown once. the app keeps a shared preference value of false when the user has already opened the app once
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isFirstTime = sharedPreferences.getBoolean("first_time", true)

        val welcomeLayout = view.findViewById<LinearLayout>(R.id.welcome_layout)
        val welcomeTextView = view.findViewById<TextView>(R.id.welcome_textview)
        val createProfileTextView = view.findViewById<TextView>(R.id.create_profile_to_start_textview)
        val createProfile = view.findViewById<Button>(R.id.create_profile_from_home)
        val playChess = view.findViewById<Button>(R.id.playChess)

        if (isFirstTime) {
            welcomeTextView.visibility = View.VISIBLE
        }

        viewModel.hasProfiles.observe(viewLifecycleOwner, Observer {hasProfiles ->
            if (!hasProfiles){
                createProfileTextView.visibility = View.VISIBLE
                createProfile.visibility = View.VISIBLE
            } else {
                welcomeLayout.visibility = View.GONE
            }
        })

        //this waits for the CreateProfile activity's result. if the returned value is true it means that a profile has been created
        val profileCreationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val profileCreated = data?.getBooleanExtra("profileCreated", false) ?: false

                if (profileCreated) {
                    viewModel.onProfileCreationInitiated()
                    sharedPreferences.edit().putBoolean("first_time", false).apply()
                    welcomeLayout.visibility = View.GONE
                }
            }
        }

        //this is called when the user clicks the createProfile button
        viewModel.initiateProfileCreation.observe(viewLifecycleOwner, Observer { initiate ->
            if (initiate){
                val createProfileIntent = Intent(requireContext(), CreateProfile::class.java)
                profileCreationLauncher.launch(createProfileIntent)
            }
        })

        createProfile.setOnClickListener {
            viewModel.initiateProfileCreation()
        }

        playChess.setOnClickListener {
            playChess.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    playChess.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            val playIntent = Intent(requireContext(), PlayActivity::class.java)
            startActivity(playIntent)
        }

        viewModel.checkProfiles()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }
}