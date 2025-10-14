package com.unit.tools.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unit.tools.R
import com.unit.tools.ui.theme.UnitToolsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = stringResource(id = R.string.cd_app_logo),
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit // ou Inside
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Home Screen")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    UnitToolsTheme {
        HomeScreen()
    }
}
