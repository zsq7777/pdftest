package com.example.myapplication.pdfviewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
internal fun DefaultController(controller: PdfController) {
    var jumpPage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // 顶部页码显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${controller.currentPage + 1}/${controller.pageCount}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 底部控制栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = controller::previousPage,
                enabled = controller.currentPage > 0
            ) {
                Icon(Icons.Default.ArrowBack, "Previous")
            }

            OutlinedTextField(
                value = jumpPage,
                onValueChange = { jumpPage = it },
                modifier = Modifier.width(120.dp),
                placeholder = { Text("Jump to") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        jumpPage.toIntOrNull()?.let {
                            controller.jumpToPage(it - 1)
                        }
                    }) {
                        Icon(Icons.Default.ArrowForward, "Go")
                    }
                }
            )

            IconButton(
                onClick = controller::nextPage,
                enabled = controller.currentPage < controller.pageCount - 1
            ) {
                Icon(Icons.Default.ArrowForward, "Next")
            }
        }
    }
}

private fun shouldRenderPage(index: Int, state: PdfViewerState): Boolean {
    val first = state.listState.firstVisibleItemIndex
    val last = state.listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    return index in (first - 2)..(last + 2)
}