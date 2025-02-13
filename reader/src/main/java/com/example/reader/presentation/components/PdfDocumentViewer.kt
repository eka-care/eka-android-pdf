package com.example.reader.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.reader.data.model.PdfDocuments
import com.example.reader.data.model.PdfPage

@Composable
fun PdfDocumentViewer(pdfDocuments: PdfDocuments, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
            val maxOffsetX = (scale - 1f) * 1000
            val maxOffsetY = (scale - 1f) * 1000
            offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
            offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
        } else {
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .transformable(state = transformableState)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentPadding = PaddingValues(vertical = 16.dp),
            userScrollEnabled = scale <= 1f
        ) {
            items(pdfDocuments.pageCount) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(elevation = 2.dp)
                        .background(Color.White)
                ) {
                    val pageState by produceState<PdfPage?>(
                        initialValue = null,
                        pdfDocuments,
                        pageIndex
                    ) {
                        value = pdfDocuments.getPage(pageIndex)
                    }

                    pageState?.let { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(page.width.toFloat() / page.height.toFloat())
                        ) {
                            val bitmap by produceState<Bitmap?>(initialValue = null, page) {
                                value = page.render(1f)
                            }

                            bitmap?.let { renderedBitmap ->
                                Image(
                                    bitmap = renderedBitmap.asImageBitmap(),
                                    contentDescription = "PDF Page ${pageIndex + 1}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } ?: CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}