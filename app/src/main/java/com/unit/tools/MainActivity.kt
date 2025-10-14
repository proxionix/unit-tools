package com.unit.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.unit.tools.ui.navigation.AppNavHost
import com.unit.tools.ui.theme.UnitToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitToolsTheme {
                Surface { AppNavHost() }
            }
        }
    }
}
