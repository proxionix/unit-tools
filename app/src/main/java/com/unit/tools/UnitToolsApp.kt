package com.unit.tools

import android.app.Application
import com.unit.tools.data.SettingsDataStore
import com.unit.tools.i18n.AppLocaleManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Application class to apply the saved per-app locale as early as possible.
 * This ensures the initial UI is rendered in the correct language on cold start.
 */
class UnitToolsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Read persisted app locale synchronously and apply before any Activity renders UI
        val store = SettingsDataStore(applicationContext)
        val tag = runBlocking { store.appLocaleFlow.first() }
        AppLocaleManager.apply(tag)
    }
}
