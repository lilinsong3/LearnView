package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoard
import kotlinx.coroutines.flow.Flow

interface DazzlingBoardRepository {
    fun getDazzlingBoardStream(): Flow<DazzlingBoard>
    fun saveDazzlingBoardStream(dazzlingBoard: DazzlingBoard): Flow<Unit>
}