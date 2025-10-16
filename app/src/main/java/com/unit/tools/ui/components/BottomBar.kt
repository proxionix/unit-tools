package com.unit.tools.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.unit.tools.R
import com.unit.tools.ui.navigation.Routes
import com.unit.tools.ui.theme.UnitToolsTheme

private val BrandSelected = Color(0xFFC65E16)

@Composable
fun BottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    alwaysShowLabel: Boolean = true
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = BrandSelected,
        selectedTextColor = BrandSelected,
        indicatorColor = BrandSelected.copy(alpha = 0.12f)
        // Les couleurs "unselected" laissent M3 s'adapter au thème;
        // si besoin: unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        //           unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { 
                if (currentRoute != Routes.HOME) {
                    onNavigate(Routes.HOME)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(id = R.string.cd_home)
                )
            },
            label = { Text(stringResource(id = R.string.nav_home)) },
            alwaysShowLabel = alwaysShowLabel,
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == Routes.ORDER,
            onClick = { 
                if (currentRoute != Routes.ORDER) {
                    onNavigate(Routes.ORDER)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = stringResource(id = R.string.nav_order)
                )
            },
            label = { Text(stringResource(id = R.string.nav_order)) },
            alwaysShowLabel = alwaysShowLabel,
            colors = itemColors
        )
        NavigationBarItem(
            selected = currentRoute == Routes.SETTINGS,
            onClick = { 
                if (currentRoute != Routes.SETTINGS) {
                    onNavigate(Routes.SETTINGS)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.cd_settings)
                )
            },
            label = { Text(stringResource(id = R.string.nav_settings)) },
            alwaysShowLabel = alwaysShowLabel,
            colors = itemColors
        )
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    UnitToolsTheme {
        BottomBar(currentRoute = Routes.HOME, onNavigate = {})
    }
}
