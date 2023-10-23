package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import kotlinx.coroutines.flow.Flow

interface DazzlingBoardRepository {
    fun getDazzlingBoardStream(): Flow<DazzlingBoardModel>
    suspend fun saveSlogan(slogan: String)
    suspend fun saveBackgroundColor(backgroundColor: Int)
    suspend fun saveSloganColor(sloganColor: Int)
    suspend fun saveSloganSize(sloganSize: Float)
    suspend fun saveFlashing(flashing: Boolean)
    suspend fun saveRolling(rolling: Boolean)
}