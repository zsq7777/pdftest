package com.example.myapplication.pdfviewer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue



@Composable
fun rememberPdfViewerState() = remember {
    PdfViewerState()
}

class PdfViewerState {
    internal val listState = LazyListState()
    var currentPage by mutableIntStateOf(0)
        private set  // 限制外部直接修改
    var totalPages by mutableIntStateOf(0)
    var isLoaded by mutableStateOf(false)

    var onPageChanged: (Int) -> Unit = {}
        private set

    fun updateCurrentPage(page: Int) {
        currentPage = page
        onPageChanged(page)
    }
}