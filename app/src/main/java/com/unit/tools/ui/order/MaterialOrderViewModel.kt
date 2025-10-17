package com.unit.tools.ui.order

import android.content.Context
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

    // Recherche / tri / filtres / vue
    enum class Sort { Relevance, Name, Max, Stock }

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

    /**
     * Modifie la quantité d'un produit (index 1-based: 1..24)
     */
    fun setQuantity(index1Based: Int, value: Int) {
        if (index1Based in 1..24) {
            val idx = index1Based - 1
            val max = _catalog.value.getOrNull(idx)?.max ?: Int.MAX_VALUE
            _quantities.update { old ->
                old.copyOf().also { it[idx] = value.coerceIn(0, max) }
            }
        }
    }

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

    /** Incrémente de 1 en respectant le max du produit. */
    fun increment(code: String, max: Int) {
        val idx = indexByCode[code] ?: return
        _quantities.update { cur ->
            cur.copyOf().also { it[idx] = (it[idx] + 1).coerceAtMost(max) }
        }
    }

    /** Décrémente de 1 sans passer sous 0. */
    fun decrement(code: String) {
        val idx = indexByCode[code] ?: return
        _quantities.update { cur ->
            cur.copyOf().also { it[idx] = (it[idx] - 1).coerceAtLeast(0) }
        }
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

