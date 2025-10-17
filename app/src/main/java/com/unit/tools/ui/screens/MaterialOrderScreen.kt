package com.unit.tools.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unit.tools.R
import com.unit.tools.pdf.PdfOrderFiller
import com.unit.tools.share.EmailSender
import com.unit.tools.share.PdfPreviewer
import com.unit.tools.share.PreviewActionBus
import com.unit.tools.ui.components.FilterBar
import com.unit.tools.ui.components.OrderRow
import com.unit.tools.ui.components.SearchTopBar
import com.unit.tools.ui.components.bus.SearchBarBus
import com.unit.tools.ui.order.MaterialOrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MaterialOrderScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialOrderScreen(
    viewModel: MaterialOrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Read catalog via VM helper when needed; avoid unused local state
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val visible by viewModel.visibleItems.collectAsState(initial = emptyList())
    // Quantities accessed via viewModel.quantityOf(code) per item
    val inStockOnly by viewModel.inStockOnly.collectAsState()

    // Charger le catalogue + lier actions top bar
    LaunchedEffect(Unit) {
        viewModel.loadCatalog(context)
        PreviewActionBus.onPreviewRequest = {
            val techName = viewModel.fullName()
            val maxima = viewModel.maxima()
            val qtyMap = viewModel.quantitiesMapClamped()
            scope.launch {
                try {
                    val file = withContext(Dispatchers.IO) {
                        PdfOrderFiller.fillOrderPdf(
                            context = context,
                            technicianName = techName,
                            quantities = qtyMap,
                            maxima = maxima
                        )
                    }
                    PdfPreviewer.open(context, file)
                } catch (e: Exception) {
                    Log.e(TAG, "Preview PDF error", e)
                    snackbarHostState.showSnackbar("Preview error: ${e.message}")
                }
            }
        }
        SearchBarBus.onQueryChange = { viewModel.setQuery(it) }
        SearchBarBus.onSortClick = { viewModel.setSort(MaterialOrderViewModel.Sort.Name) }
        SearchBarBus.onFilterClick = { viewModel.toggleInStock() }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { SearchTopBar(scrollBehavior = scrollBehavior) },
        bottomBar = {},
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        // Optional sticky-ish filter row just under the TopAppBar
        FilterBar(
            inStockOnly = inStockOnly,
            onToggleStock = { viewModel.toggleInStock() },
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            // Option A: liste d'abord
            items(
                items = visible,
                key = { it.code }
            ) { item ->
                // Determine current app language for per-item localized name
                val appLocales = AppCompatDelegate.getApplicationLocales()
                val lang = if (!appLocales.isEmpty) appLocales[0]?.language ?: java.util.Locale.getDefault().language else java.util.Locale.getDefault().language
                // Quantité actuelle via VM helper (évite tout mapping local)
                val value = viewModel.quantityOf(item.code)
                OrderRow(
                    name = if (lang.startsWith("nl", ignoreCase = true)) item.name_nl else item.name_fr,
                    code = item.code,
                    per = item.per,
                    max = item.max,
                    value = value,
                    onChange = { new -> viewModel.setQty(item.code, new) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
            }

            // Section Identité (compacte)
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = stringResource(id = R.string.order_identity_section), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { viewModel.setFirstName(it) },
                            label = { Text(stringResource(id = R.string.order_first_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { viewModel.setLastName(it) },
                            label = { Text(stringResource(id = R.string.order_last_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Bouton Envoyer
            item {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                if (firstName.isBlank() || lastName.isBlank()) {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.order_identity_missing)
                                    )
                                    return@launch
                                }
                                val techName = viewModel.fullName()
                                val maxima = viewModel.maxima()
                                val qtyMap = viewModel.quantitiesMapClamped()
                                val pdfFile = withContext(Dispatchers.IO) {
                                    PdfOrderFiller.fillOrderPdf(
                                        context = context,
                                        technicianName = techName,
                                        quantities = qtyMap,
                                        maxima = maxima
                                    )
                                }
                                val recipient = "warehouse_houthalen@unit-t.eu"
                                val subject = context.getString(R.string.order_email_subject)
                                val body = context.getString(R.string.order_email_body)
                                EmailSender.sendPdf(context, pdfFile, recipient, subject, body)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error generating or sending PDF", e)
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(id = R.string.order_submit)) }
            }
        }
    }
}

