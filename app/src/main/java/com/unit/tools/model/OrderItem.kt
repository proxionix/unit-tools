package com.unit.tools.model

/**
 * Représente un produit de commande matériel.
 *
 * @property code Code produit (a1..a24) utilisé comme clé PDF
 * @property name_fr Nom du produit en français
 * @property name_nl Nom du produit en néerlandais
 * @property max Quantité maximale commandable
 * @property per Quantité par unité (ex: 1000 agrafes par boîte)
 */
data class OrderItem(
    val code: String,
    val name_fr: String,
    val name_nl: String,
    val max: Int,
    val per: Int
)
