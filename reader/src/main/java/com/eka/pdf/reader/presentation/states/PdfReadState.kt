package com.eka.pdf.reader.presentation.states

import com.eka.pdf.reader.data.model.PdfDocuments

sealed class PdfReadState {
    object Idle : PdfReadState()
    object Loading : PdfReadState()
    data class Success(val pdfDocuments: PdfDocuments) : PdfReadState()
    data class Error(val message: String) : PdfReadState()
}