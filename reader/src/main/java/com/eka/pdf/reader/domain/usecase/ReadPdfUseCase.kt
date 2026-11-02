package com.eka.pdf.reader.domain.usecase

import android.net.Uri
import com.eka.pdf.reader.data.model.PdfDocuments
import com.eka.pdf.reader.domain.repository.PdfReadRepository

class ReadPdfUseCase(private val pdfReadRepository: PdfReadRepository) {
    suspend operator fun invoke(url: String, isImage: Boolean = false): PdfDocuments {
        return if (isImage) {
            pdfReadRepository.readImageFromUrl(url)
        } else {
            pdfReadRepository.readPdfFromUrl(url)
        }
    }

    suspend operator fun invoke(uri: Uri, isImage: Boolean = false): PdfDocuments {
        return if (isImage) {
            pdfReadRepository.readImageFromUri(uri)
        } else {
            pdfReadRepository.readPdfFromUri(uri)
        }
    }
}