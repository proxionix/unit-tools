package com.unit.tools.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object PdfPreviewer {
    fun open(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, null)
        context.startActivity(chooser)
    }
}
