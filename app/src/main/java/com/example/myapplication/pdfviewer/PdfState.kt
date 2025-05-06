package com.example.myapplication.pdfviewer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


/**
 * 记忆化组件状态
 */
@Composable
fun rememberPdfViewerState() = remember {
    PdfViewerState()
}

class PdfViewerState {
    internal val listState = LazyListState()
    var currentPage by mutableIntStateOf(0)
    var totalPages by mutableIntStateOf(0)
    var isLoaded by mutableStateOf(false)
}