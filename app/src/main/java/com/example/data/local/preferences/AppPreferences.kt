package com.example.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_VIBRATE = booleanPreferencesKey("vibrate_on_success")
        val KEY_SOUND = booleanPreferencesKey("sound_on_success")
        val KEY_AUTO_EXPORT = booleanPreferencesKey("auto_export")
        val KEY_THREAD_COUNT = intPreferencesKey("thread_count")
        val KEY_DEFAULT_ROUTER_ID = longPreferencesKey("default_router_id")
        val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")

        fun getLanguageSync(context: Context): String {
            return context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
                .getString("app_language", "system") ?: "system"
        }
    }

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { e ->
            timber.log.Timber.e(e, "Error reading AppPreferences DataStore")
            emit(emptyPreferences())
        }

    val vibrateOnSuccess: Flow<Boolean> = safeData.map { it[KEY_VIBRATE] ?: true }
    val soundOnSuccess: Flow<Boolean> = safeData.map { it[KEY_SOUND] ?: false }
    val autoExport: Flow<Boolean> = safeData.map { it[KEY_AUTO_EXPORT] ?: false }
    val threadCount: Flow<Int> = safeData.map { it[KEY_THREAD_COUNT] ?: 3 }
    val defaultRouterId: Flow<Long> = safeData.map { it[KEY_DEFAULT_ROUTER_ID] ?: 0L }
    val appLanguage: Flow<String> = safeData.map { it[KEY_APP_LANGUAGE] ?: "system" }

    suspend fun setAppLanguage(lang: String) {
        dataStore.edit { it[KEY_APP_LANGUAGE] = lang }
    }

    suspend fun setVibrateOnSuccess(enabled: Boolean) {
        dataStore.edit { it[KEY_VIBRATE] = enabled }
    }

    suspend fun setSoundOnSuccess(enabled: Boolean) {
        dataStore.edit { it[KEY_SOUND] = enabled }
    }

    suspend fun setDefaultRouterId(routerId: Long) {
        dataStore.edit { it[KEY_DEFAULT_ROUTER_ID] = routerId }
    }
}
