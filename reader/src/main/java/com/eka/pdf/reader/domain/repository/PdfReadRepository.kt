package com.eka.pdf.reader.domain.repository

import android.net.Uri
import com.eka.pdf.reader.data.model.PdfDocuments

interface PdfReadRepository {
    suspend fun readPdfFromUrl(url: String): PdfDocuments
    suspend fun readPdfFromUri(uri: Uri): PdfDocuments

    suspend fun readImageFromUrl(url: String): PdfDocuments
    suspend fun readImageFromUri(uri: Uri): PdfDocuments
}