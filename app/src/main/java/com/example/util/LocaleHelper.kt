package com.example.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {
    private const val PREF_LANG = "app_language"
    private const val PREF_NAME = "locale_prefs"

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = try {
            when (languageCode) {
                "ar" -> Locale("ar")
                "en" -> Locale("en")
                else -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Resources.getSystem().configuration.locales[0]
                    } else {
                        @Suppress("DEPRECATION")
                        Resources.getSystem().configuration.locale
                    }
                }
            }
        } catch (_: Exception) {
            Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getPersistedLocale(context: Context): String {
        return try {
            val defPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val defaultLang = defPref.getString("app_language", "system") ?: "system"
            if (defaultLang != "system") {
                defaultLang
            } else {
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .getString(PREF_LANG, "system") ?: "system"
            }
        } catch (_: Exception) {
            "system"
        }
    }

    private fun persistLocale(context: Context, lang: String) {
        try {
            val defPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            defPref.edit().putString("app_language", lang).apply()
        } catch (_: Exception) {}
        try {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(PREF_LANG, lang).apply()
        } catch (_: Exception) {}
    }
}
