package com.unit.tools.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Type scale resserrée pour lisibilité et densité maîtrisée
val AppTypography = Typography(
    displayLarge = TextStyle(           // Titres de page
        fontSize = 48.sp,
        lineHeight = 58.sp,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TextStyle(          // En-têtes section
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(            // Titres cartes / listes
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(             // Texte standard
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(             // Labels de boutons
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium
    )
)
