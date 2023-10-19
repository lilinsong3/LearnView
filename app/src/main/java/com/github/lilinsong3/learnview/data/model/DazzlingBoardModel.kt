package com.github.lilinsong3.learnview.data.model

data class DazzlingBoardModel(
    // TODO: 修改默认值
    val text: String = "",
    val backgroundColor: Int = 0x000000,
    val textColor: Int = 0x000000,
    val textSize: Int = 48,
    val flashing: Boolean = true,
    val rolling: Boolean = true
)
