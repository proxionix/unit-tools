package com.unit.tools.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object EmailSender {

    /**
     * Ouvre l'application d'email pour envoyer un PDF en pièce jointe.
     *
     * @param context Context Android
     * @param pdfFile Le fichier PDF à envoyer
     * @param recipientEmail L'adresse email du destinataire
     * @param subject Le sujet de l'email
     * @param body Le corps du message
     */
    fun sendPdf(
        context: Context,
        pdfFile: File,
        recipientEmail: String,
        subject: String,
        body: String
    ) {
        // Obtenir l'URI du fichier via FileProvider
        val pdfUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        // Créer l'intent d'envoi d'email
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Lancer le chooser pour permettre à l'utilisateur de choisir l'app d'email
        val chooserIntent = Intent.createChooser(emailIntent, null)
        context.startActivity(chooserIntent)
    }
}
