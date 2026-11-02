package com.example.reader.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.example.reader.data.model.PdfDocuments
import com.example.reader.data.model.PdfPage
import com.example.reader.domain.repository.PdfReadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class PdfReaderImpl(private val context: Context) : PdfReadRepository {

    override suspend fun readPdfFromUrl(url: String): PdfDocuments = withContext(Dispatchers.IO) {
        val file = downloadPdfFromUrl(url)
        readPdfFromFile(file)
    }

    override suspend fun readPdfFromUri(uri: Uri): PdfDocuments = withContext(Dispatchers.IO) {
        val file = copyUriToFile(uri)
        readPdfFromFile(file)
    }

    override suspend fun readImageFromUrl(url: String): PdfDocuments = withContext(Dispatchers.IO) {
        val file = downloadImageFromUrl(url)
        readImageFromFile(file)
    }

    override suspend fun readImageFromUri(uri: Uri): PdfDocuments = withContext(Dispatchers.IO) {
        val file = copyUriToFile(uri)
        readImageFromFile(file)
    }

    private suspend fun downloadPdfFromUrl(url: String): File = withContext(Dispatchers.IO) {
        val destination = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
        URL(url).openStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        destination
    }

    private suspend fun downloadImageFromUrl(url: String): File = withContext(Dispatchers.IO) {
        val destination = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}")
        try {
            URL(url).openStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to download image from URL: $url", e)
        }
        destination
    }

    private suspend fun copyUriToFile(uri: Uri): File = withContext(Dispatchers.IO) {
        val destination = File(context.cacheDir, "temp_media_${System.currentTimeMillis()}")
        context.contentResolver.openInputStream(uri)?.use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        destination
    }

    private suspend fun readPdfFromFile(file: File): PdfDocuments = withContext(Dispatchers.IO) {
        val pdfRenderer = PdfRenderer(file.parcelFileDescriptor())
        val pageCount = pdfRenderer.pageCount

        PdfDocuments(
            pageCount = pageCount,
            getPage = { pageIndex ->
                PdfPage(
                    width = pdfRenderer.openPage(pageIndex).use { it.width },
                    height = pdfRenderer.openPage(pageIndex).use { it.height },
                    render = { scale ->
                        pdfRenderer.openPage(pageIndex).use { page ->
                            val bitmap = Bitmap.createBitmap(
                                (page.width * scale).toInt(),
                                (page.height * scale).toInt(),
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                            bitmap
                        }
                    }
                )
            },
            close = {
                file.delete()
            }
        )
    }

    private suspend fun readImageFromFile(file: File): PdfDocuments = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(file.path)
            ?: throw IllegalArgumentException("Failed to decode image from file: ${file.path}")

        PdfDocuments(
            pageCount = 1,
            getPage = { _ ->
                PdfPage(
                    width = bitmap.width,
                    height = bitmap.height,
                    render = { _ ->
                        bitmap
                    }
                )
            },
            close = {
                file.delete()
            }
        )
    }

    private fun File.parcelFileDescriptor() =
        android.os.ParcelFileDescriptor.open(this, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
}