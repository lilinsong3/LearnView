package com.github.lilinsong3.learnview.data.repository

import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import kotlinx.coroutines.flow.Flow

interface DazzlingBoardRepository {
    fun getDazzlingBoardStream(): Flow<DazzlingBoardModel>
    suspend fun saveSlogan(slogan: String): Unit
    suspend fun saveBackgroundColor(backgroundColor: Int): Unit
    suspend fun saveSloganColor(sloganColor: Int): Unit
    suspend fun saveSloganSize(sloganSize: Float): Unit
    suspend fun saveFlashing(flashing: Boolean): Unit
    suspend fun saveRolling(rolling: Boolean): Unit
}