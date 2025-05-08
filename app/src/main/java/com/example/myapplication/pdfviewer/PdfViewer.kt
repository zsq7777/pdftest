package com.example.myapplication.pdfviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.pdfviewer.internal.PdfController
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl
import com.example.myapplication.pdfviewer.zoom.EnhancedPageItem

/**
 * PDF查看器主组件
 * @param data 支持文件路径、File对象或Uri
 * @param modifier 样式修饰符
 * @param state 组件状态（可选）
 * @param controller 自定义控制器（可选）
 */
@Composable
fun PdfViewer(
    data: Any,
    modifier: Modifier = Modifier,
    state: PdfViewerState = rememberPdfViewerState()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val controller = remember { PdfControllerImpl(context, data, state, coroutineScope) }

    DisposableEffect(controller) {
        onDispose { controller.close() }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxWidth()
    ) {
        items(controller.pageCount) { index ->
            EnhancedPageItem(
                controller = controller,
                pageIndex = index,
                scrollState = scrollState,
                coroutineScope = coroutineScope
            )
        }
    }
}