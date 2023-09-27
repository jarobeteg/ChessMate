package com.example.chessmate.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chessmate.ui.fragment.HomeFragment
import com.example.chessmate.ui.fragment.LearnFragment
import com.example.chessmate.ui.fragment.MoreFragment
import com.example.chessmate.ui.fragment.ProfileFragment
import com.example.chessmate.ui.fragment.PuzzlesFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> HomeFragment()
            1 -> PuzzlesFragment()
            2 -> LearnFragment()
            3 -> ProfileFragment()
            4 -> MoreFragment()
            else -> HomeFragment()
        }
    }
}