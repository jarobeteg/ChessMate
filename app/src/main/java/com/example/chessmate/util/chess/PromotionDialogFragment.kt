package com.example.chessmate.util.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.chessmate.R

class PromotionDialogFragment(private val isWhiteStarting: Boolean, private val listener: PromotionDialogListener,
                              private val sourceSquare: Square, private val destinationSquare: Square ) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_promotion_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val promoteToQueen = view.findViewById<ImageButton>(R.id.buttonPromoteToQueen)
        val promoteToRook = view.findViewById<ImageButton>(R.id.buttonPromoteToRook)
        val promoteToBishop = view.findViewById<ImageButton>(R.id.buttonPromoteToBishop)
        val promoteToKnight = view.findViewById<ImageButton>(R.id.buttonPromoteToKnight)

        if (isWhiteStarting) {
            promoteToQueen.setImageResource(R.drawable.default_queen_white)
            promoteToRook.setImageResource(R.drawable.default_rook_white)
            promoteToBishop.setImageResource(R.drawable.default_bishop_white)
            promoteToKnight.setImageResource(R.drawable.default_knight_white)
        } else {
            promoteToQueen.setImageResource(R.drawable.default_queen_black)
            promoteToRook.setImageResource(R.drawable.default_rook_black)
            promoteToBishop.setImageResource(R.drawable.default_bishop_black)
            promoteToKnight.setImageResource(R.drawable.default_knight_black)
        }

        promoteToQueen.setOnClickListener {
            listener.onPieceSelected(PieceType.QUEEN, sourceSquare, destinationSquare)
            dismiss()
        }

        promoteToRook.setOnClickListener {
            listener.onPieceSelected(PieceType.ROOK, sourceSquare, destinationSquare)
            dismiss()
        }

        promoteToBishop.setOnClickListener {
            listener.onPieceSelected(PieceType.BISHOP, sourceSquare, destinationSquare)
            dismiss()
        }

        promoteToKnight.setOnClickListener {
            listener.onPieceSelected(PieceType.KNIGHT, sourceSquare, destinationSquare)
            dismiss()
        }
    }

    interface PromotionDialogListener {
        fun onPieceSelected(pieceType: PieceType, sourceSquare: Square, destinationSquare: Square)
    }
}