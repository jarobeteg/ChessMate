package com.example.chessmate.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chessmate.R
import com.example.chessmate.ui.viewmodel.PuzzlesViewModel

class PuzzlesFragment : Fragment() {

    companion object {
        fun newInstance() = PuzzlesFragment()
    }

    private lateinit var viewModel: PuzzlesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_puzzles, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PuzzlesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}