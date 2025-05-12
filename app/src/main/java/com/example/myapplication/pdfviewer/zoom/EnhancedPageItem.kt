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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    coroutineScope: CoroutineScope
) {
    val zoomState = remember { ZoomState() }

    // 渲染页面位图
    val bitmap = remember(pageIndex) {
        (controller as PdfControllerImpl).renderPage(pageIndex).apply {
            // 更新内容尺寸
            zoomState.contentSize = IntSize(width, height)
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // 添加父容器左右边距

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
                .pdfGesture(zoomState, scrollState, coroutineScope, constraints, bitmapScale)
                .fillMaxWidth()
        )
    }
}

