package com.example.myapplication.pdfviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.myapplication.pdfviewer.internal.PdfController
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl

@Composable
internal fun PageContent(
    controller: PdfController,
    state: PdfViewerState
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = state.listState) {
        items(controller.pageCount) { index ->
            if (shouldRenderPage(index, state)) {
                PageItem(controller, index)
            } else {
                LoadingPlaceholder()
            }
        }
    }
}

@Composable
private fun PageItem(controller: PdfController, index: Int) {
    val bitmap = remember(index) { (controller as PdfControllerImpl).renderPage(index) }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Page ${index + 1}",
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat()),
         contentScale = ContentScale.FillWidth
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(800.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}


private fun shouldRenderPage(index: Int, state: PdfViewerState): Boolean {
    val first = state.listState.firstVisibleItemIndex
    val last = state.listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return index in (first - 2)..(last + 2)
}