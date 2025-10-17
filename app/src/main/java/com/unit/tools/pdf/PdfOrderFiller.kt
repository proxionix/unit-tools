package com.unit.tools.pdf

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfOrderFiller {

    private const val TAG = "PdfOrderFiller"

    /**
     * Remplit un PDF de commande (FR ou NL selon la langue per-app) avec le nom du technicien
     * et les quantités a1..a24, applique les maxima, et retourne le fichier PDF généré.
     *
     * @param context Context Android
     * @param technicianName Nom complet du technicien (prénom + nom)
     * @param quantities Map des quantités par champ (ex: "a1" -> 5)
     * @param maxima Map des maxima par champ (ex: "a1" -> 10)
     * @return Le fichier PDF généré dans le cache
     */
    fun fillOrderPdf(
        context: Context,
        technicianName: String,
        quantities: Map<String, Int>,
        maxima: Map<String, Int>
    ): File {
        // Initialiser PDFBox pour Android
        PDFBoxResourceLoader.init(context)

        // Déterminer la langue per-app (pas la locale système)
        val isDutch = isApplicationLanguageDutch()
        
        // Sélectionner le template selon la langue
        val templateName = if (isDutch) "NL-Materiel.pdf" else "FR-Materiel.pdf"
        
        Log.d(TAG, "Using template: $templateName (isDutch=$isDutch)")

        // Charger le template depuis les assets et traiter le document
        context.assets.open(templateName).use { templateStream ->
            PDDocument.load(templateStream).use { document ->
                // Récupérer le formulaire AcroForm
                val acroForm: PDAcroForm? = document.documentCatalog.acroForm

                if (acroForm != null) {
                    // Liste des champs attendus pour validation
                    val expectedFields = mutableSetOf("name_tech")
                    for (i in 1..24) {
                        expectedFields.add("a$i")
                    }
                    
                    // Lister les champs disponibles
                    val availableFields = acroForm.fields.map { it.fullyQualifiedName }.toSet()
                    
                    // Détecter les champs manquants
                    val missingFields = expectedFields - availableFields
                    if (missingFields.isNotEmpty()) {
                        Log.w(TAG, "Missing PDF fields in $templateName: ${missingFields.sorted()}")
                    }
                    
                    // Remplir le champ name_tech
                    val techNameField = acroForm.getField("name_tech")
                    if (techNameField != null) {
                        techNameField.setValue(technicianName.trim())
                        Log.d(TAG, "Set name_tech: '${technicianName.trim()}'")
                    } else {
                        Log.w(TAG, "Field 'name_tech' not found in PDF")
                    }

                    // Remplir les champs a1..a24
                    for (i in 1..24) {
                        val key = "a$i"
                        val rawValue = quantities[key] ?: 0
                        val maxValue = maxima[key] ?: 999
                        val clampedValue = rawValue.coerceIn(0, maxValue)

                        val field = acroForm.getField(key)
                        if (field != null) {
                            field.setValue(clampedValue.toString())
                            Log.v(TAG, "Set $key: $clampedValue (raw=$rawValue, max=$maxValue)")
                        } else {
                            Log.w(TAG, "Field '$key' not found in PDF")
                        }
                    }

                    // Aplatir le formulaire pour que les valeurs soient fixes
                    acroForm.flatten()
                } else {
                    Log.e(TAG, "No AcroForm found in $templateName")
                }

                // Générer un nom de fichier unique avec timestamp
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val outputFileName = "CommandeMateriel_$timestamp.pdf"
                val outputFile = File(context.cacheDir, outputFileName)

                // Sauvegarder le document
                document.save(outputFile)
                
                Log.d(TAG, "PDF generated: ${outputFile.absolutePath}")

                return outputFile
            }
        }
    }
    
    /**
     * Détermine si la langue de l'application (per-app) est le néerlandais.
     * Utilise AppCompatDelegate.getApplicationLocales() pour respecter le choix de langue dans l'app.
     */
    private fun isApplicationLanguageDutch(): Boolean {
        val appLocales: LocaleListCompat = AppCompatDelegate.getApplicationLocales()
        
        // Si une locale per-app est définie, l'utiliser
        if (!appLocales.isEmpty) {
            val primaryLocale = appLocales[0]
            val language = primaryLocale?.language ?: ""
            Log.d(TAG, "App locale detected: $language")
            return language.startsWith("nl", ignoreCase = true)
        }
        
        // Fallback sur la locale système si aucune locale per-app
        val systemLanguage = Locale.getDefault().language
        Log.d(TAG, "No app locale, using system: $systemLanguage")
        return systemLanguage.startsWith("nl", ignoreCase = true)
    }
}

