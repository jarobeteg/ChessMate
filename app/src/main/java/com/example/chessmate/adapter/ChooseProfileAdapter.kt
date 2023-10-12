package com.example.chessmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmate.R
import com.example.chessmate.database.entity.UserProfile
import android.widget.ImageButton
import android.widget.LinearLayout

class ChooseProfileAdapter(private val profiles: List<UserProfile>): RecyclerView.Adapter<ChooseProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.choose_profile_username)
        val levelTextView: TextView = itemView.findViewById(R.id.choose_profile_level)
        val openingRatingTextView: TextView = itemView.findViewById(R.id.choose_profile_opening_rating)
        val midgameRatingTextView: TextView = itemView.findViewById(R.id.choose_profile_midgame_rating)
        val endgameRatingTextView: TextView = itemView.findViewById(R.id.choose_profile_endgame_rating)
        val chooseProfileButton: ImageButton = itemView.findViewById(R.id.choose_profile_button)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linear_layout)
        val expandButton: ImageButton = itemView.findViewById(R.id.expand_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.choose_profiles, parent, false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val currentProfile = profiles[position]
        holder.usernameTextView.text = currentProfile.username
        holder.levelTextView.text = currentProfile.level.toString()
        holder.openingRatingTextView.text = currentProfile.openingRating.toString()
        holder.midgameRatingTextView.text = currentProfile.midgameRating.toString()
        holder.endgameRatingTextView.text = currentProfile.endgameRating.toString()

        holder.linearLayout.translationY = -holder.linearLayout.height.toFloat()
        holder.linearLayout.alpha = 0f

        holder.expandButton.rotation = 0f

        holder.expandButton.setOnClickListener {
            if (holder.linearLayout.visibility == View.VISIBLE) {
                holder.linearLayout.animate()
                    .translationY(-holder.linearLayout.height.toFloat())
                    .alpha(0.0f)
                    .setDuration(500)
                    .withEndAction {
                        holder.linearLayout.visibility = View.GONE
                    }

                holder.expandButton.animate()
                    .rotation(0f).duration = 500
            } else {
                holder.linearLayout.visibility = View.VISIBLE
                holder.linearLayout.animate()
                    .translationY(0f)
                    .alpha(1.0f).duration = 500

                holder.expandButton.animate()
                    .rotation(-90f).duration = 500
            }
        }

        holder.chooseProfileButton.setOnClickListener {
            //todo
        }
    }

    override fun getItemCount(): Int {
        return profiles.size
    }
}
