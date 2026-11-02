package com.eka.pdf.reader

import android.app.Application
import android.content.Context
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.eka.pdf.reader.data.repository.PdfReaderImpl
import com.eka.pdf.reader.domain.repository.PdfReadRepository
import com.eka.pdf.reader.domain.usecase.ReadPdfUseCase
import com.eka.pdf.reader.presentation.components.PdfDocumentViewer
import com.eka.pdf.reader.presentation.states.PdfReadState
import com.eka.pdf.reader.presentation.states.PdfSource
import com.eka.pdf.reader.presentation.viewmodel.ReaderViewModel

class PdfReaderManager(context: Context) {

    private val readPdfRepository: PdfReadRepository = PdfReaderImpl(context)
    private val readPdfUseCase: ReadPdfUseCase = ReadPdfUseCase(readPdfRepository)
    private val viewModel: ReaderViewModel =
        ReaderViewModel(context.applicationContext as Application, readPdfUseCase)

    @Composable
    fun PdfViewer(
        pdfSource: PdfSource,
        isImage: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        val pdfState by viewModel.pdfState.collectAsState()

        LaunchedEffect(pdfSource) {
            viewModel.readPdf(pdfSource, isImage)
        }

        when (val state = pdfState) {
            is PdfReadState.Idle -> {
                // Show idle state UI
            }
            is PdfReadState.Loading -> {
                CircularProgressIndicator()
            }
            is PdfReadState.Success -> {
                PdfDocumentViewer(state.pdfDocuments, modifier)
            }
            is PdfReadState.Error -> {
                Text("Error: ${state.message}")
            }
        }
    }

}