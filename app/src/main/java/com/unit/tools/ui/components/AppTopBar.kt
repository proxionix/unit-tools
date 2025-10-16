package com.unit.tools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unit.tools.R
import androidx.compose.material3.TopAppBarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarWithLogo() {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
        },
        actions = {},
        // Remplacement de centerAlignedTopAppBarColors() -> topAppBarColors()
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBarTitle() {
    TopAppBar(
        title = { Text(stringResource(id = R.string.nav_settings)) },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTopBarTitle(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onPreviewClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.order_title)) },
        colors = TopAppBarDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior,
        actions = {
            val click = onPreviewClick ?: com.unit.tools.share.PreviewActionBus.onPreviewRequest
            if (click != null) {
                IconButton(onClick = click) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = stringResource(id = R.string.cd_preview_pdf)
                    )
                }
            }
        }
    )
}

