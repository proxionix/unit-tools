package com.unit.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.unit.tools.data.SettingsDataStore
import com.unit.tools.i18n.AppLocaleManager
import com.unit.tools.ui.navigation.AppNavHost
import com.unit.tools.ui.theme.UnitToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settings = remember { SettingsDataStore(context.applicationContext) }
            val appLocale by settings.appLocaleFlow.collectAsState(initial = "system")

            LaunchedEffect(appLocale) {
                AppLocaleManager.apply(appLocale)
            }

            UnitToolsTheme(darkTheme = isSystemInDarkTheme()) {
                Surface { AppNavHost() }
            }
        }
    }
}
