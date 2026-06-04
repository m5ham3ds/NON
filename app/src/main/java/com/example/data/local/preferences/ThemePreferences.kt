package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

class ThemePreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")

        fun getThemeModeSync(context: Context): String {
            return context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
                .getString("theme_mode", "system") ?: "system"
        }
    }

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { e ->
            timber.log.Timber.e(e, "Error reading ThemePreferences DataStore")
            emit(emptyPreferences())
        }

    val themeMode: Flow<String> = safeData.map { it[KEY_THEME_MODE] ?: "system" }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[KEY_THEME_MODE] = mode }
    }
}
