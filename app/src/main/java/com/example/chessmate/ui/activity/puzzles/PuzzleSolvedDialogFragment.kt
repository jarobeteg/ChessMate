package com.example.chessmate.ui.activity.puzzles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.chessmate.databinding.FragmentPuzzleSolvedDialogBinding

class PuzzleSolvedDialogFragment : DialogFragment() {
    private var _binding: FragmentPuzzleSolvedDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: OnNextPuzzleButtonListener? = null

    interface OnNextPuzzleButtonListener {
        fun onNextPuzzleButtonListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNextPuzzleButtonListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNextPuzzleButtonClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPuzzleSolvedDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toNextPuzzle.setOnClickListener {
            listener?.onNextPuzzleButtonListener()
            dismiss()
        }

        dialog?.setCancelable(false)
        isCancelable = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}