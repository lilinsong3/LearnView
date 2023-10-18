package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoard
import kotlinx.coroutines.flow.Flow

class DefaultDazzlingBoardRepository: DazzlingBoardRepository {
    override fun getDazzlingBoardStream(): Flow<DazzlingBoard> {
        TODO("Not yet implemented")
    }

    override fun saveDazzlingBoardStream(dazzlingBoard: DazzlingBoard): Flow<Unit> {
        TODO("Not yet implemented")
    }
}