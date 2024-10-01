package com.example.chessmate.ui.activity.lessons

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.chessmate.databinding.FragmentLessonFinishedDialogBinding

class LessonFinishedDialogFragment : DialogFragment() {
    private var _binding: FragmentLessonFinishedDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: OnNextLessonButtonListener? = null

    interface OnNextLessonButtonListener {
        fun onNextLessonButtonListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNextLessonButtonListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNextLessonButtonClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonFinishedDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toNextLesson.setOnClickListener {
            listener?.onNextLessonButtonListener()
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