package com.example.chessmate.ui.activity.lessons

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chessmate.R
import androidx.fragment.app.DialogFragment
import com.example.chessmate.databinding.FragmentCoordinateSettingsDialogBinding
import com.example.chessmate.util.InputFilterMinMax

class CoordinateSettingsDialogFragment : DialogFragment() {
    private var _binding: FragmentCoordinateSettingsDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: OnSaveButtonListener? = null

    interface OnSaveButtonListener {
        fun onSaveButtonListener(showCoordinates: Boolean, showPieces: Boolean, playAsWhite: Boolean, minutes: Int, seconds: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSaveButtonListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSaveButtonListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoordinateSettingsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.minutesInput.filters = arrayOf(InputFilterMinMax(0, 5))
        binding.secondsInput.filters = arrayOf(InputFilterMinMax(0, 59))

        binding.saveCoordinateSettings.setOnClickListener {
            val minutes = binding.minutesInput.text.toString().toIntOrNull() ?: 0
            val seconds = binding.secondsInput.text.toString().toIntOrNull() ?: 0
            val showCoordinates = binding.showCoordinates.isChecked
            val showPieces = binding.showPieces.isChecked
            val playAsWhite = binding.playAs.checkedRadioButtonId == R.id.play_as_white

            listener?.onSaveButtonListener(showCoordinates, showPieces, playAsWhite, minutes, seconds)
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}