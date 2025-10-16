package com.unit.tools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unit.tools.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarWithLogo() {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
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

