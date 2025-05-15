package com.example.myapplication.pdfviewer.internal

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.collection.LruCache
import androidx.compose.runtime.*
import com.example.myapplication.pdfviewer.PdfViewerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

interface PdfController {
    val pageCount: Int
    val currentPage: Int
    val isReady: Boolean
    fun jumpToPage(page: Int, smooth: Boolean = false)
    fun previousPage()
    fun nextPage()
    fun reload(newData: Any)
    val dataVersion: Int


}

internal class PdfControllerImpl(
    private val context: Context,
    private val data: Any,
    private val state: PdfViewerState,
    private val coroutineScope: CoroutineScope
) : PdfController {
    private var renderer = PdfRendererHelper(context, data)
    private var pageCache = LruCache<Int, Bitmap>(5)




    init {
        state.totalPages = renderer.pageCount
        state.isLoaded = true
        // 启动协程监听滚动位置
        coroutineScope.launch {
            snapshotFlow { state.listState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
                    val currentPage = visibleItems
                        .firstOrNull { item ->
                            // 判断项是否占据屏幕中心区域
                            val itemCenter = item.offset + item.size / 2
                            itemCenter in (state.listState.layoutInfo.viewportStartOffset + 100)..(state.listState.layoutInfo.viewportEndOffset - 100)
                        }?.index ?: 0

                    state.updateCurrentPage(currentPage)
                }
        }
    }
    override var dataVersion by mutableStateOf(0)
        private set
    override val pageCount: Int
        get() = state.totalPages

    override val currentPage: Int
        get() = state.currentPage

    override val isReady: Boolean
        get() = state.isLoaded

    override fun jumpToPage(page: Int, smooth: Boolean) {
        if (page !in 0 until pageCount) {
            return
        }
        coroutineScope.launch {
            try {
                if (smooth) {
                    state.listState.animateScrollToItem(page)
                } else {
                    state.listState.scrollToItem(page)
                }
            } catch (e: Exception) {
                state.listState.scrollToItem(page)
            }
        }
    }

    override fun reload(newData: Any) {
        close()
        renderer = PdfRendererHelper(context, newData)
        pageCache.evictAll()
        state.totalPages = renderer.pageCount
        state.isLoaded = true
        dataVersion++  // 每次reload递增版本号
    }

    override fun previousPage() = jumpToPage(currentPage - 1)
    override fun nextPage() = jumpToPage(currentPage + 1)


    internal fun renderPage(page: Int): Bitmap {
        val bitmap = pageCache[page] ?: renderer.renderPage(page)
        pageCache.put(page, bitmap)
        return bitmap
    }

    internal fun close() {
        renderer.close()
        pageCache.evictAll()
    }
}