package com.gdmie

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    private const val PREFS = "GDMIE_LANGUAGE"
    private const val KEY_LANGUAGE = "language"

    const val ENGLISH = "en"
    const val KANNADA = "kn"
    const val HINDI = "hi"
    const val TAMIL = "ta"
    const val TELUGU = "te"
    const val MALAYALAM = "ml"

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, ENGLISH) ?: ENGLISH
    }

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun apply(context: Context): Context {
        val language = getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }
}
