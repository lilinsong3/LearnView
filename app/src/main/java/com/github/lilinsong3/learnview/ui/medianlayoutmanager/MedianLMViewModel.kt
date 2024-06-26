package com.github.lilinsong3.learnview.ui.medianlayoutmanager

import androidx.annotation.StyleRes
import androidx.lifecycle.ViewModel
import com.github.lilinsong3.learnview.R
import com.github.lilinsong3.learnview.common.Differentiable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.concurrent.fixedRateTimer

class MedianLMViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TextItemsUiState.fakeInitUiState())
    val uiState: StateFlow<TextItemsUiState> = _uiState.asStateFlow()

    fun roll() {
        fixedRateTimer("RollTimer", false, 3000, 3000) {
//            if (_uiState.value.highlightPosition == _uiState.value.items.lastIndex) {
//                cancel()
//            } else {
                _uiState.update { state ->
                    val nextPosition = if (state.highlightPosition == state.items.size - 1) 0 else state.highlightPosition + 1
                    val updateItems = state.items.toMutableList()

                    if (state.highlightPosition > -1) {
                        updateItems[state.highlightPosition] =
                            updateItems[state.highlightPosition].copy(textAppearanceRes = R.style.MedianText)
                    }

                    updateItems[nextPosition] =
                        updateItems[nextPosition].copy(textAppearanceRes = R.style.HighlightMedianText)

                    state.copy(items = updateItems, highlightPosition = nextPosition)
                }
//            }
        }
    }
}

data class TextItemUiState(
    val id: Int, val content: String, @StyleRes val textAppearanceRes: Int = R.style.MedianText
) : Differentiable {
    override fun getKey(): String = id.toString()
}

data class TextItemsUiState(val items: List<TextItemUiState>, val highlightPosition: Int = if (items.isEmpty()) -1 else 0) {

    companion object {
        fun fakeInitUiState(): TextItemsUiState = TextItemsUiState(items = List(50) { index ->
            TextItemUiState(
                index, "item_$index", if (index == 0) R.style.HighlightMedianText else R.style.MedianText
            )
        })
    }
}