package com.github.lilinsong3.learnview.ui.animatetext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.lilinsong3.learnview.data.repository.AnimateTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimateTextViewModel @Inject constructor(
    private val animateTextRepository: AnimateTextRepository
) : ViewModel() {
    val animateTextModelFlow = animateTextRepository.getAnimateTextStream()
    fun inputText(text: String) {
        viewModelScope.launch {
            animateTextRepository.saveSlogan(text)
        }
    }

    fun slideBackgroundColor(color: Int) {
        viewModelScope.launch {
            animateTextRepository.saveBackgroundColor(color)
        }
    }

    fun slideTextColor(textColor: Int) {
        viewModelScope.launch {
            animateTextRepository.saveSloganColor(textColor)
        }
    }

    fun slideTextSize(textSize: Float) {
        viewModelScope.launch {
            animateTextRepository.saveSloganSize(textSize)
        }
    }

    fun switchFlashing(flashing: Boolean) {
        viewModelScope.launch {
            animateTextRepository.saveFlashing(flashing)
        }
    }

    fun switchRolling(rolling: Boolean) {
        viewModelScope.launch {
            animateTextRepository.saveRolling(rolling)
        }
    }

    fun reset() {
        viewModelScope.launch {
            animateTextRepository.reset()
        }
    }
}