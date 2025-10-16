package com.unit.tools.data

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

private const val DATA_STORE_NAME = "user_settings"
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

class SettingsDataStore(private val context: Context) {
    companion object {
        private val APP_LOCALE = stringPreferencesKey("APP_LOCALE")
        val AllowedLocale = setOf("system", "fr", "nl", "en")
    }

    val appLocaleFlow: Flow<String> = context.settingsDataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it[APP_LOCALE] ?: "system" }

    suspend fun setAppLocale(tag: String) {
        require(AllowedLocale.contains(tag)) { "Invalid language tag: $tag" }
        context.settingsDataStore.edit { it[APP_LOCALE] = tag }
    }
}
