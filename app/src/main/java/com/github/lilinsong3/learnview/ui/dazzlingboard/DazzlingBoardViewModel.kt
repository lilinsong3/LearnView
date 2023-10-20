package com.github.lilinsong3.learnview.ui.dazzlingboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.lilinsong3.learnview.data.repository.DazzlingBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DazzlingBoardViewModel @Inject constructor(
    private val dazzlingBoardRepository: DazzlingBoardRepository
) : ViewModel() {
    val dazzlingBoardModelFlow = dazzlingBoardRepository.getDazzlingBoardStream()
    fun inputText(text: String) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveSlogan(text)
        }
    }

    fun slideBackgroundColor(color: Int) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveBackgroundColor(color)
        }
    }

    fun slideTextColor(textColor: Int) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveSloganColor(textColor)
        }
    }

    fun slideTextSize(textSize: Float) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveSloganSize(textSize)
        }
    }

    fun switchFlashing(flashing: Boolean) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveFlashing(flashing)
        }
    }

    fun switchRolling(rolling: Boolean) {
        viewModelScope.launch {
            dazzlingBoardRepository.saveRolling(rolling)
        }
    }
}