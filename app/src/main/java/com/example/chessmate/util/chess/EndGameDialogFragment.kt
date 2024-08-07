package com.example.chessmate.util.chess

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.chessmate.R
import com.example.chessmate.databinding.FragmentEndGameDialogBinding

class EndGameDialogFragment(private val endGameResult: String) : DialogFragment() {

    private var _binding: FragmentEndGameDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: OnHomeButtonClickListener? = null

    interface OnHomeButtonClickListener {
        fun onHomeButtonClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHomeButtonClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHomeButtonClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEndGameDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (endGameResult) {
            "player_checkmated" -> binding.endGameResult.text = getString(R.string.game_lost)
            "bot_checkmated" -> binding.endGameResult.text = getString(R.string.game_won)
            else -> binding.endGameResult.text = getString(R.string.game_tie)
        }

        binding.toHomepage.setOnClickListener {
            listener?.onHomeButtonClicked()
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