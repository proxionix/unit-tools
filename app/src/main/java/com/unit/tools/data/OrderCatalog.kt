package com.unit.tools.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unit.tools.model.OrderItem

/**
 * Charge et valide le catalogue de produits depuis materials_order.json
 */
object OrderCatalog {
    
    private const val TAG = "OrderCatalog"
    private const val ASSET_FILE = "materials_order.json"
    private const val EXPECTED_COUNT = 24
    
    /**
     * Charge le catalogue depuis le fichier JSON assets.
     * Valide qu'il contient exactement 24 items avec les codes a1..a24 dans l'ordre.
     *
     * @param context Context Android
     * @return Liste des 24 OrderItem
     * @throws IllegalStateException si le fichier est invalide
     */
    fun load(context: Context): List<OrderItem> {
        try {
            val json = context.assets.open(ASSET_FILE).use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }
            
            val type = object : TypeToken<List<OrderItem>>() {}.type
            val items: List<OrderItem> = Gson().fromJson(json, type)
            
            // Validation : nombre d'items
            if (items.size != EXPECTED_COUNT) {
                val msg = "Expected $EXPECTED_COUNT items, found ${items.size}"
                Log.e(TAG, msg)
                throw IllegalStateException(msg)
            }
            
            // Validation : codes a1..a24 dans l'ordre
            val expectedCodes = (1..EXPECTED_COUNT).map { "a$it" }
            val actualCodes = items.map { it.code }
            
            if (actualCodes != expectedCodes) {
                val msg = "Expected codes $expectedCodes, found $actualCodes"
                Log.w(TAG, msg)
                // Log warning mais ne pas crasher, permet une certaine flexibilité
            }
            
            // Validation : aucun champ vide
            items.forEachIndexed { index, item ->
                if (item.code.isBlank() || item.name_fr.isBlank() || item.name_nl.isBlank()) {
                    Log.w(TAG, "Item $index has empty fields: $item")
                }
                if (item.max <= 0 || item.per <= 0) {
                    Log.w(TAG, "Item $index has invalid max or per: $item")
                }
            }
            
            Log.d(TAG, "Successfully loaded $EXPECTED_COUNT items from $ASSET_FILE")
            return items
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load $ASSET_FILE", e)
            throw IllegalStateException("Cannot load order catalog", e)
        }
    }
}
