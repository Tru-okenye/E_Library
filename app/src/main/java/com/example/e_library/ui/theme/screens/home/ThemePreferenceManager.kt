package com.example.e_library.ui.theme.screens.home

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreferenceManager {
    private const val PREFERENCES_NAME = "theme_preferences"
    private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    suspend fun saveDarkModePreference(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    fun getDarkModePreference(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[DARK_MODE_KEY] ?: false // Default to light theme
            }
    }
}
