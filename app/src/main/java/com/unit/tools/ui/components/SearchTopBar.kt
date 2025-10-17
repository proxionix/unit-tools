package com.unit.tools.ui.components
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.outlined.Visibility
import com.unit.tools.R
import com.unit.tools.share.PreviewActionBus
import com.unit.tools.ui.components.bus.SearchBarBus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {
    val (query, setQuery) = remember { mutableStateOf("") }

    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    setQuery(it)
                    SearchBarBus.onQueryChange?.invoke(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                placeholder = { Text(stringResource(id = R.string.order_search_hint)) }
            )
        },
        actions = {
            IconButton(onClick = { SearchBarBus.onSortClick?.invoke() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = stringResource(id = R.string.cd_sort))
            }
            IconButton(onClick = { SearchBarBus.onFilterClick?.invoke() }) {
                Icon(Icons.Filled.FilterList, contentDescription = stringResource(id = R.string.cd_filter))
            }
            IconButton(onClick = { PreviewActionBus.onPreviewRequest?.invoke() }) {
                Icon(Icons.Outlined.Visibility, contentDescription = stringResource(id = R.string.cd_preview_pdf))
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}
