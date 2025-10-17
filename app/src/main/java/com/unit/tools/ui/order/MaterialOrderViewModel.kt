package com.unit.tools.ui.order

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.unit.tools.data.OrderCatalog
import com.unit.tools.model.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialOrderViewModel : ViewModel() {
    companion object { private const val TAG = "MaterialOrderVM" }

    // Catalogue des produits (chargé depuis JSON)
    private val _catalog = MutableStateFlow<List<OrderItem>>(emptyList())
    val catalog: StateFlow<List<OrderItem>> = _catalog.asStateFlow()

    // État du formulaire
    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    // Quantités pour les 24 produits (index 0..23 correspond à a1..a24)
    private val _quantities = MutableStateFlow(IntArray(24) { 0 })
    val quantities: StateFlow<IntArray> = _quantities.asStateFlow()

    // Mapping code -> index (a1..a24)
    private val indexByCode: Map<String, Int> by lazy {
        (1..24).associate { i -> "a$i" to (i - 1) }
    }

    init {
        // Debug-time assert to ensure mapping covers a1..a24
        check(indexByCode.size == 24) { "indexByCode must contain 24 entries for a1..a24" }
    }

    // Recherche / tri / filtres / vue
    enum class Sort { Relevance, Name, Max }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _sort = MutableStateFlow(Sort.Relevance)
    val sort: StateFlow<Sort> = _sort.asStateFlow()

    private val _inStockOnly = MutableStateFlow(false)
    val inStockOnly: StateFlow<Boolean> = _inStockOnly.asStateFlow()

    // Liste visible selon recherche/tri/filtres
    val visibleItems: StateFlow<List<OrderItem>> = combine(
        catalog, query, sort, inStockOnly
    ) { items, q, s, stockOnly ->
        var list = items
        if (q.isNotBlank()) {
            val ql = q.trim().lowercase()
            list = list.filter { it.name_fr.lowercase().contains(ql) || it.name_nl.lowercase().contains(ql) || it.code.lowercase().contains(ql) }
        }
        if (stockOnly) {
            // Sans backend stock: approx via max > 0
            list = list.filter { it.max > 0 }
        }
        when (s) {
            Sort.Name -> list.sortedBy { it.name_fr }
            Sort.Max -> list.sortedByDescending { it.max }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mutations UI
    fun setQuery(value: String) { _query.value = value }
    fun setSort(value: Sort) { _sort.value = value }
    fun toggleInStock() { _inStockOnly.value = !_inStockOnly.value }

    // Note: previous helpers (qtyFor/setQty/displayNameFromItem) removed after refactor

    /**
     * Charge le catalogue depuis les assets
     */
    fun loadCatalog(context: Context) {
        if (_catalog.value.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val loaded = OrderCatalog.load(context)
                _catalog.value = loaded

                // Validate catalog codes after load
                val expected = (1..24).map { "a$it" }.toSet()
                val codes = loaded.map { it.code }.toSet()
                val missing = expected - codes
                val extras = codes - expected
                if (missing.isNotEmpty() || extras.isNotEmpty()) {
                    Log.w(
                        TAG,
                        "Catalog codes mismatch. Missing=${missing.joinToString()} Extras=${extras.joinToString()}"
                    )
                }
            }
        }
    }

    /**
     * Modifie le prénom
     */
    fun setFirstName(value: String) {
        _firstName.value = value
    }

    /**
     * Modifie le nom
     */
    fun setLastName(value: String) {
        _lastName.value = value
    }

    // setQuantity(index1Based, value) removed; UI uses setQty(code, value)

    /**
     * Met à jour la quantité via le code produit (ex: "a1").
     * Recommandé côté UI pour éviter toute ambiguïté d'index.
     */
    fun setQty(code: String, value: Int) {
        val idx = indexByCode[code] ?: return
        val max = _catalog.value.getOrNull(idx)?.max ?: Int.MAX_VALUE
        _quantities.update { cur ->
            cur.copyOf().also { it[idx] = value.coerceIn(0, max) }
        }
    }

    // increment/decrement helpers are unnecessary with onValueChange driving setQty; removed to avoid unused warnings

    /** Retourne la quantité courante pour un code produit donné. */
    fun quantityOf(code: String): Int {
        val idx = indexByCode[code] ?: return 0
        return _quantities.value.getOrElse(idx) { 0 }
    }

    /**
     * Retourne le nom complet (prénom + nom)
     */
    fun fullName(): String {
        val first = _firstName.value.trim()
        val last = _lastName.value.trim()
        return when {
            first.isNotEmpty() && last.isNotEmpty() -> "$first $last"
            first.isNotEmpty() -> first
            last.isNotEmpty() -> last
            else -> ""
        }
    }

    // Note: name selection is handled directly in the UI based on current app locale

    /**
     * Retourne les maxima depuis le catalogue
     * Map avec clés "a1".."a24" et leurs maxima respectifs
     */
    fun maxima(): Map<String, Int> {
        if (_catalog.value.isEmpty()) {
            return (1..24).associate { "a$it" to 999 }
        }
        
        return _catalog.value.associate { it.code to it.max }
    }

    /**
     * Retourne les quantités sous forme de Map clampées selon les maxima du catalogue
     */
    fun quantitiesMapClamped(): Map<String, Int> {
        if (_catalog.value.isEmpty()) {
            return (1..24).associate { 
                val key = "a$it"
                key to _quantities.value[it - 1].coerceIn(0, 999)
            }
        }
        
        return _catalog.value.mapIndexed { index, item ->
            val value = _quantities.value[index]
            item.code to value.coerceIn(0, item.max)
        }.toMap()
    }
}

