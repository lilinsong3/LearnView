package com.github.lilinsong3.learnview.ui.dazzlingboard

import androidx.lifecycle.ViewModel
import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import com.github.lilinsong3.learnview.data.repository.DazzlingBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DazzlingBoardViewModel @Inject constructor(
    private val dazzlingBoardRepository: DazzlingBoardRepository
) : ViewModel() {
    private val _dazzlingBoardModelFlow = MutableStateFlow(DazzlingBoardModel())
    val dazzlingBoardModelFlow = _dazzlingBoardModelFlow.asStateFlow()
}