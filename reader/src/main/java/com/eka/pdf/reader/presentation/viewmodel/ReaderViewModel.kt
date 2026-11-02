package com.eka.pdf.reader.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eka.pdf.reader.domain.usecase.ReadPdfUseCase
import com.eka.pdf.reader.presentation.states.PdfReadState
import com.eka.pdf.reader.presentation.states.PdfSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReaderViewModel(app: Application, private val readPdfUseCase: ReadPdfUseCase) :
    AndroidViewModel(app) {

    private val _pdfState = MutableStateFlow<PdfReadState>(PdfReadState.Idle)
    val pdfState: StateFlow<PdfReadState> = _pdfState

    fun readPdf(source: PdfSource, isImage: Boolean = false) {
        _pdfState.value = PdfReadState.Loading

        viewModelScope.launch {
            try {
                val pdfDocuments = when (source) {
                    is PdfSource.Url -> readPdfUseCase(source.url, isImage)
                    is PdfSource.Uri -> readPdfUseCase(source.uri, isImage)
                }
                _pdfState.value = PdfReadState.Success(pdfDocuments)
            } catch (e: Exception) {
                _pdfState.value = PdfReadState.Error(e.message ?: "Unknown error")
            }
        }
    }

}