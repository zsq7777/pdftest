package com.example.myapplication.pdfviewer.zoom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    val context = LocalContext.current
    val zoomState = remember { ZoomState() }

    // 渲染页面位图
    val bitmap = remember(pageIndex) {
        (controller as PdfControllerImpl).renderPage(pageIndex).apply {
            // 更新内容尺寸
            zoomState.contentSize = IntSize(width, height)
        }
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val bitmapScale = bitmap.width.toFloat() / bitmap.height.toFloat()
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Page ${pageIndex + 1}",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .pdfGesture(zoomState, scrollState, coroutineScope,constraints,bitmapScale)
                .fillMaxWidth()
        )
    }


    Spacer(modifier = Modifier.height(8.dp))
}


/*

//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//
//    ) {
//        Image(
//            bitmap = bitmap.asImageBitmap(),
//            contentDescription = "Page ${pageIndex + 1}",
//            contentScale = ContentScale.FillWidth,
//            modifier = Modifier
//                .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
//                .pdfGesture(zoomState, scrollState, coroutineScope)
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(4.dp)))
//    }*/
