package com.example.chessmate.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OpeningRepo (
    val lessonId: Int,
    val titleId: Int,
    val descriptionTitleId: List<Int>,
    val descriptionId: List<Int>,
    val fen: String,
    val solution: String
) : Parcelable