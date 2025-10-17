package com.unit.tools.share

/**
 * Bus très simple pour déclencher une action d'aperçu PDF depuis la Top App Bar
 * vers l'écran de commande, sans couplage fort.
 */
object PreviewActionBus {
    @Volatile
    var onPreviewRequest: (() -> Unit)? = null
}
