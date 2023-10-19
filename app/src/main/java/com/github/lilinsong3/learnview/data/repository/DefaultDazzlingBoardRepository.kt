package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import kotlinx.coroutines.flow.Flow

class DefaultDazzlingBoardRepository: DazzlingBoardRepository {
    override fun getDazzlingBoardStream(): Flow<DazzlingBoardModel> {
        TODO("Not yet implemented")
    }

    override fun saveDazzlingBoardStream(dazzlingBoardModel: DazzlingBoardModel): Flow<Unit> {
        TODO("Not yet implemented")
    }
}