package com.example.myapplication.pdfviewer.zoom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.myapplication.pdfviewer.internal.PdfController
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl
import kotlinx.coroutines.CoroutineScope

@Composable
fun EnhancedPageItem(
    controller: PdfController,
    pageIndex: Int,
    scrollState: LazyListState,
) {
    val zoomState = remember { ZoomState() }

    val bitmap = remember(pageIndex, controller.dataVersion) {
        (controller as PdfControllerImpl).renderPage(pageIndex).apply {
            zoomState.contentSize = IntSize(width, height)
        }
    }

    LaunchedEffect(controller.dataVersion) {
        zoomState.scale = 1f
        zoomState.offset = Offset.Zero
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {
        val bitmapScale = bitmap.width.toFloat() / bitmap.height.toFloat()
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Page ${pageIndex + 1}",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,
                )
                .pdfGesture(zoomState, scrollState, constraints, bitmapScale)
                .fillMaxWidth()
        )
    }
}

