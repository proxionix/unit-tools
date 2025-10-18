package com.unit.tools

import android.app.Application
import android.util.Log
import com.unit.tools.data.SettingsDataStore
import com.unit.tools.i18n.AppLocaleManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Application class to apply the saved per-app locale as early as possible.
 * This ensures the initial UI is rendered in the correct language on cold start.
 */
class UnitToolsApp : Application() {
    private companion object {
        const val TAG = "UnitToolsApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "========================================")
        Log.d(TAG, "UnitToolsApp.onCreate() called")
        Log.d(TAG, "========================================")

        // Read persisted app locale synchronously and apply before any Activity renders UI
        val store = SettingsDataStore(applicationContext)
        val tag = runBlocking { store.appLocaleFlow.first() }

        Log.d(TAG, "Loaded locale from DataStore: '$tag'")

        // Check current AppCompat locales before applying
        val currentAppCompatLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        Log.d(TAG, "Current AppCompat locales BEFORE apply: ${currentAppCompatLocales.toLanguageTags()}")

        AppLocaleManager.apply(tag)

        val afterAppCompatLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        Log.d(TAG, "Current AppCompat locales AFTER apply: ${afterAppCompatLocales.toLanguageTags()}")
        Log.d(TAG, "========================================")
    }
}
