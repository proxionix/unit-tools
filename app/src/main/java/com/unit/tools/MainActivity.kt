package com.unit.tools

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import com.unit.tools.ui.navigation.AppNavHost
import com.unit.tools.ui.theme.UnitToolsTheme

class MainActivity : ComponentActivity() {
    private companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "========================================")
        Log.d(TAG, "MainActivity.onCreate() called")
        Log.d(TAG, "savedInstanceState: ${if (savedInstanceState == null) "null (fresh start)" else "NOT NULL (recreated)"}")

        val currentLocales = AppCompatDelegate.getApplicationLocales()
        Log.d(TAG, "Current AppCompat locales: ${currentLocales.toLanguageTags()}")
        Log.d(TAG, "========================================")

        enableEdgeToEdge()
        setContent {
            UnitToolsTheme(darkTheme = isSystemInDarkTheme()) {
                Surface { AppNavHost() }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "MainActivity.onDestroy() called")
        super.onDestroy()
    }
}
