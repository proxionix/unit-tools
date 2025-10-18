package com.unit.tools.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.unit.tools.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unit.tools.ui.theme.UnitToolsTheme

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(id = R.string.home_title))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    UnitToolsTheme {
        HomeScreen()
    }
}
