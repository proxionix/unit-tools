package com.unit.tools.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unit.tools.R

/**
 * Ligne compacte Material 3 pour un article de commande.
 * Affiche le nom du produit, les métadonnées (code, max, per) et un stepper.
 *
 * @param name Nom localisé du produit (FR/NL)
 * @param code Code du produit (ex: "a1", "a24")
 * @param per Nombre d'unités par paquet
 * @param max Quantité maximum commandable
 * @param value Quantité actuellement sélectionnée
 * @param onChange Callback de modification de la quantité
 */
@Composable
fun OrderRow(
    name: String,
    code: String,
    per: Int,
    max: Int,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Informations produit (nom + métadonnées)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.order_row_meta, code, max, per),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Stepper compact pour saisie quantité (32dp boutons, 36dp chiffre)
        QuantityStepperCompact(
            value = value,
            onValueChange = { newValue -> onChange(newValue.coerceIn(0, max)) },
            min = 0,
            max = max
        )
    }
}
