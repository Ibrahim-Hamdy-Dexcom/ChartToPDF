package com.example.charttopdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException

object PDFHandler {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun convertBitMapToPDF(
        context: Context,
        uri: Uri,
        bitmap: Bitmap,
        callback: () -> Unit
    ) {
        val pageHeight = bitmap.height
        val pageWidth = bitmap.width
        Bitmap.createScaledBitmap(bitmap, pageWidth, pageHeight, true)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val outputStream = context.contentResolver.openOutputStream(uri)
        try {
            pdfDocument.writeTo(outputStream)
            callback()
        } catch (ex: IOException) {
            Log.e("TAG", "convertBitMapToPDF: ", ex)
        }
        pdfDocument.close()
    }

    fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(
            view.left,
            view.top,
            view.right,
            view.bottom
        )
        view.draw(canvas)
        return bitmap
    }
}

class CreateSpecificTypeDocument(private val type: String) :
    ActivityResultContracts.CreateDocument() {
    override fun createIntent(context: Context, input: String): Intent {
        return super.createIntent(context, input).setType(type)
    }
}