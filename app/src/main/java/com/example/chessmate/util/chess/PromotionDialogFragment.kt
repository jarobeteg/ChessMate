package com.example.chessmate.util.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.chessmate.R
import com.example.chessmate.util.ChessThemeUtil
import com.example.chessmate.util.chess.bitboard.BitSquare

class PromotionDialogFragment(private val isPlayerWhite: Boolean, private val listener: PromotionDialogListener,
                              private val fromSquare: BitSquare, private val toSquare: BitSquare) : DialogFragment() {
    companion object {
        const val TAG = "PromotionDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_promotion_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chessThemeUtil = ChessThemeUtil(requireContext())
        val pieceThemeArray = chessThemeUtil.getPieceTheme()

        val promoteToQueen = view.findViewById<ImageButton>(R.id.buttonPromoteToQueen)
        val promoteToRook = view.findViewById<ImageButton>(R.id.buttonPromoteToRook)
        val promoteToBishop = view.findViewById<ImageButton>(R.id.buttonPromoteToBishop)
        val promoteToKnight = view.findViewById<ImageButton>(R.id.buttonPromoteToKnight)

        if (isPlayerWhite) {
            promoteToQueen.setImageResource(pieceThemeArray[4])
            promoteToRook.setImageResource(pieceThemeArray[3])
            promoteToBishop.setImageResource(pieceThemeArray[2])
            promoteToKnight.setImageResource(pieceThemeArray[1])
        } else {
            promoteToQueen.setImageResource(pieceThemeArray[10])
            promoteToRook.setImageResource(pieceThemeArray[9])
            promoteToBishop.setImageResource(pieceThemeArray[8])
            promoteToKnight.setImageResource(pieceThemeArray[7])
        }

        promoteToQueen.setOnClickListener {
            listener.onPieceSelected(PieceType.QUEEN, fromSquare, toSquare)
            dismiss()
        }

        promoteToRook.setOnClickListener {
            listener.onPieceSelected(PieceType.ROOK, fromSquare, toSquare)
            dismiss()
        }

        promoteToBishop.setOnClickListener {
            listener.onPieceSelected(PieceType.BISHOP, fromSquare, toSquare)
            dismiss()
        }

        promoteToKnight.setOnClickListener {
            listener.onPieceSelected(PieceType.KNIGHT, fromSquare, toSquare)
            dismiss()
        }
    }

    interface PromotionDialogListener {
        fun onPieceSelected(pieceType: PieceType, fromSquare: BitSquare, toSquare: BitSquare)
    }
}