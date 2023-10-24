package com.github.lilinsong3.learnview.data.repository

import android.content.Context
import android.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.github.lilinsong3.learnview.data.model.DazzlingBoardModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dazzlingBoardDataStore: DataStore<Preferences> by preferencesDataStore(name = "dazzlingBoard")

class DefaultDazzlingBoardRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    DazzlingBoardRepository {

    companion object {
        val TEXT = stringPreferencesKey("text")
        val BACKGROUND_COLOR = intPreferencesKey("background_color")
        val TEXT_COLOR = intPreferencesKey("text_color")
        val TEXT_SIZE = floatPreferencesKey("text_size")
        val FLASHING = booleanPreferencesKey("flashing")
        val ROLLING = booleanPreferencesKey("rolling")
    }

    override fun getDazzlingBoardStream(): Flow<DazzlingBoardModel> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            DazzlingBoardModel(
                preferences[TEXT] ?: "夺目牌",
                preferences[BACKGROUND_COLOR] ?: Color.BLACK,
                preferences[TEXT_COLOR] ?: Color.WHITE,
                preferences[TEXT_SIZE] ?: 48f,
                preferences[FLASHING] ?: true,
                preferences[ROLLING] ?: true,
            )
        }

    override suspend fun saveSlogan(slogan: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[TEXT] = slogan
        }
    }

    override suspend fun saveBackgroundColor(backgroundColor: Int) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[BACKGROUND_COLOR] = backgroundColor
        }
    }

    override suspend fun saveSloganColor(sloganColor: Int) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[TEXT_COLOR] = sloganColor
        }
    }

    override suspend fun saveSloganSize(sloganSize: Float) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[TEXT_SIZE] = sloganSize
        }
    }

    override suspend fun saveFlashing(flashing: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[FLASHING] = flashing
        }
    }

    override suspend fun saveRolling(rolling: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[ROLLING] = rolling
        }
    }

    override suspend fun reset() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[TEXT] = "夺目牌"
            mutablePreferences[BACKGROUND_COLOR] = Color.BLACK
            mutablePreferences[TEXT_COLOR] = Color.WHITE
            mutablePreferences[TEXT_SIZE] = 48f
            mutablePreferences[FLASHING] = true
            mutablePreferences[ROLLING] = true
        }
    }
}