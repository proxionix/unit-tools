package com.unit.tools.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unit.tools.R
import com.unit.tools.data.SettingsDataStore
import com.unit.tools.i18n.AppLocaleManager
import com.unit.tools.ui.theme.UnitToolsTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val store = remember { SettingsDataStore(context.applicationContext) }
    val appLocale by store.appLocaleFlow.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Titre retiré - déjà affiché dans la Top App Bar

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.settings_lang_group),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LanguageDropdown(
            currentTag = appLocale,
            onSelect = { tag ->
                scope.launch {
                    android.util.Log.d("SettingsScreen", "=== Language change requested: '$tag' ===")

                    // 1. Sauvegarder la nouvelle locale dans DataStore
                    android.util.Log.d("SettingsScreen", "Step 1: Saving to DataStore")
                    store.setAppLocale(tag)

                    // 2. Appliquer la locale via AppCompatDelegate
                    // Cela déclenche automatiquement la recréation de l'Activity
                    android.util.Log.d("SettingsScreen", "Step 2: Calling AppLocaleManager.apply('$tag')")
                    AppLocaleManager.apply(tag)

                    android.util.Log.d("SettingsScreen", "Step 3: AppLocaleManager.apply() completed, Activity should recreate automatically")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(
    currentTag: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        "system" to stringResource(R.string.settings_lang_system),
        "fr" to stringResource(R.string.settings_lang_fr),
        "nl" to stringResource(R.string.settings_lang_nl),
        "en" to stringResource(R.string.settings_lang_en)
    )
    val currentLabel = options.firstOrNull { it.first == currentTag }?.second ?: currentTag
    val langGroupLabel = stringResource(R.string.settings_lang_group)

    // Texte pour l'accessibilité
    val accessibilityLabel = stringResource(R.string.settings_lang_accessibility, currentLabel)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Champ "exposé": non éditable, ouvre/ferme le menu
        TextField(
            value = currentLabel,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
                .semantics {
                    contentDescription = accessibilityLabel
                },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            placeholder = { Text(langGroupLabel) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val opts = listOf(
                "system" to stringResource(R.string.settings_lang_system),
                "fr" to stringResource(R.string.settings_lang_fr),
                "nl" to stringResource(R.string.settings_lang_nl),
                "en" to stringResource(R.string.settings_lang_en)
            )
            val activeColor = MaterialTheme.colorScheme.primary
            val normalColor = MaterialTheme.colorScheme.onSurface

            opts.forEach { (tag, label) ->
                val isActive = tag == currentTag
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(id = R.string.cd_selected),
                                    tint = activeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                            } else {
                                // Réserver l'espace pour aligner le texte
                                Spacer(Modifier.width(30.dp))
                            }
                            Text(
                                text = label,
                                color = if (isActive) activeColor else normalColor
                            )
                        }
                    },
                    onClick = {
                        onSelect(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    UnitToolsTheme {
        SettingsScreen()
    }
}
