package com.github.lilinsong3.learnview.data.model

import androidx.annotation.ColorInt

data class AnimateTextModel(
    val text: String,
    @ColorInt
    val backgroundColor: Int,
    @ColorInt
    val textColor: Int,
    val textSize: Float,
    val flashing: Boolean,
    val rolling: Boolean
)
