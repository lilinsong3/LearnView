package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import kotlinx.coroutines.flow.Flow

interface DazzlingBoardRepository {
    fun getDazzlingBoardStream(): Flow<DazzlingBoardModel>
    fun saveDazzlingBoardStream(dazzlingBoardModel: DazzlingBoardModel): Flow<Unit>
}