package com.unit.tools.ui.components.bus

/** Simple bus to connect SearchTopBar with screen/ViewModel callbacks without nav wiring. */
object SearchBarBus {
    @Volatile var onQueryChange: ((String) -> Unit)? = null
    @Volatile var onSortClick: (() -> Unit)? = null
    @Volatile var onFilterClick: (() -> Unit)? = null
}
