package com.github.lilinsong3.learnview.ui.dazzlingboard

import androidx.lifecycle.ViewModel
import com.github.lilinsong3.learnview.data.repository.DazzlingBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DazzlingBoardViewModel @Inject constructor(dazzlingBoardRepository: DazzlingBoardRepository): ViewModel() {
}