package com.hosnimal.ui.on_boarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnBoardingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    // Setup variable
    private val onBoardingKey = booleanPreferencesKey(KEY)

    fun getOnBoardingSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[onBoardingKey] ?: false
        }
    }

    suspend fun saveOnBoardingSetting(isOnBoardingCleared: Boolean) {
        dataStore.edit { preferences ->
            preferences[onBoardingKey] = isOnBoardingCleared
        }
    }

    companion object {
        private const val KEY = "hosnimal_on_boarding_cleared"

        @Volatile
        private var INSTANCE: OnBoardingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): OnBoardingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = OnBoardingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}