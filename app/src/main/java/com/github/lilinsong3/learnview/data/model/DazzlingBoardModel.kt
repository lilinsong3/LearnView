package com.github.lilinsong3.learnview.data.model

import androidx.annotation.ColorInt

data class DazzlingBoardModel(
    // TODO: 修改默认值
    val text: String,
    @ColorInt
    val backgroundColor: Int,
    @ColorInt
    val textColor: Int,
    val textSize: Float,
    val flashing: Boolean,
    val rolling: Boolean
)
