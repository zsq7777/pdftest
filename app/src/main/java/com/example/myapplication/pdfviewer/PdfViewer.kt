package com.example.myapplication.pdfviewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.pdfviewer.internal.PdfController
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl
import com.example.myapplication.pdfviewer.zoom.EnhancedPageItem

/**
 * PDF查看器主组件
 * @param data 支持文件路径、File对象或Uri
 * @param modifier 样式修饰符
 * @param state 组件状态（可选）
 */
@Composable
fun PdfViewer(
    data: Any,
    modifier: Modifier = Modifier,
    state: PdfViewerState = rememberPdfViewerState()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val controller = remember { PdfControllerImpl(context, data, state, coroutineScope) }


    DisposableEffect(controller) {
        onDispose { controller.close() }
    }
    Column {
        Row(modifier = Modifier.padding(top = 40.dp)) {
            Text("首页：${controller.currentPage}", modifier = Modifier.clickable {
                controller.jumpToPage(0)
            }, fontSize = 25.sp)
            Text("上一页：${controller.currentPage}", modifier = Modifier.clickable {
                controller.previousPage()
            }, fontSize = 25.sp)
            Text("下一页：${controller.currentPage}", modifier = Modifier.clickable {
                controller.nextPage()
            }, fontSize = 25.sp)

            Text("最后一页：${controller.currentPage}", modifier = Modifier.clickable {
                controller.jumpToPage(controller.pageCount - 1)
            }, fontSize = 25.sp)
        }


        LazyColumn(
            state = state.listState,
            modifier = modifier.fillMaxWidth(),
            // 设置行间距
            verticalArrangement = Arrangement.spacedBy(8.dp),
            // 设置首尾间距
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(controller.pageCount) { index ->
                EnhancedPageItem(
                    controller = controller,
                    pageIndex = index,
                    scrollState = state.listState,
                    coroutineScope = coroutineScope
                )
            }
        }
    }

}
