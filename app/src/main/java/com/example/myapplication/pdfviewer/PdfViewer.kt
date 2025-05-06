package com.example.myapplication.pdfviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.pdfviewer.internal.PdfController
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl

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
    state: PdfViewerState = rememberPdfViewerState(),
    controller: @Composable (PdfController) -> Unit = { DefaultController(it) }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pdfController = remember { PdfControllerImpl(context, data, state,coroutineScope) }

    DisposableEffect(pdfController) {
        onDispose { pdfController.close() }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        PageContent(pdfController, state)
//        controller(pdfController)
    }
}
