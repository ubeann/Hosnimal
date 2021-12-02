package com.hosnimal.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.hosnimal.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    // Setup variable
    private val userKey = booleanPreferencesKey(KEY)
    private val idKey = intPreferencesKey(ID_KEY)
    private val nameKey = stringPreferencesKey(NAME_KEY)
    private val emailKey = stringPreferencesKey(EMAIL_KEY)
    private val phoneKey = stringPreferencesKey(PHONE_KEY)
    private val birthDayKey = stringPreferencesKey(BIRTHDAY_KEY)

    fun getUserIsLogin(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[userKey] ?: false
        }
    }

    fun getUserSetting(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[idKey] ?: 0,
                preferences[nameKey] ?: "Belum mengisi data",
                preferences[emailKey] ?: "Belum mengisi data",
                preferences[phoneKey] ?: "Belum mengisi data",
                preferences[birthDayKey] ?: "Belum mengisi data"
            )
        }
    }

    suspend fun saveUserSetting(user: User) {
        dataStore.edit { preferences ->
            preferences[userKey] = true
            preferences[idKey] = user.id
            preferences[nameKey] = user.name.toString()
            preferences[emailKey] = user.email.toString()
            preferences[phoneKey] = user.phone.toString()
            preferences[birthDayKey] = user.birthday.toString()
        }
    }

    companion object {
        private const val KEY = "hosnimal_already_login"
        private const val ID_KEY = "hosnimal_user_id"
        private const val NAME_KEY = "hosnimal_user_name"
        private const val EMAIL_KEY = "hosnimal_user_email"
        private const val PHONE_KEY = "hosnimal_user_phone"
        private const val BIRTHDAY_KEY = "hosnimal_user_birthday"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}