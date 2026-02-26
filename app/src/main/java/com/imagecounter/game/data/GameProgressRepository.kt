package com.imagecounter.game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_progress")

class GameProgressRepository(private val context: Context) {

    companion object {
        private fun stageProgressKey(stageId: Int) =
            intPreferencesKey("stage_${stageId}_max_level")
    }

    fun getMaxCompletedLevel(stageId: Int): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[stageProgressKey(stageId)] ?: 0
        }
    }

    suspend fun saveCompletedLevel(stageId: Int, levelNumber: Int) {
        context.dataStore.edit { prefs ->
            val key = stageProgressKey(stageId)
            val current = prefs[key] ?: 0
            if (levelNumber > current) {
                prefs[key] = levelNumber
            }
        }
    }
}
