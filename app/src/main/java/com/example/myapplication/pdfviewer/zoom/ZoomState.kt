package com.example.myapplication.pdfviewer.zoom

// ZoomState.kt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class ZoomState {
    // 核心状态
    var scale by mutableStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    var isZooming by mutableStateOf(false)

    // 内容尺寸（需在渲染时更新）
    var contentSize by mutableStateOf(IntSize.Zero)

    // 最大/最小缩放限制
    val minScale = 1f
    private val maxScale = 5f

    // 双击切换缩放
    fun toggleZoom() {
        scale = if (scale > minScale) minScale else 2.5f
        if (scale == minScale) offset = Offset.Zero
    }

    // 应用缩放增量
    fun applyScaleDelta(center: Offset, delta: Float) {
        val newScale = (scale * delta).coerceIn(minScale, maxScale)
        val scaleFactor = newScale / scale

        // 计算基于手势中心的偏移
        offset = Offset(
            x = offset.x * scaleFactor + (center.x * (scaleFactor - 1)),
            y = offset.y * scaleFactor + (center.y * (scaleFactor - 1))
        )

        scale = newScale
        isZooming = scale > minScale
    }

    // 调整偏移量（带边界限制）
    fun adjustOffset(delta: Offset) {
        if (!isZooming) return

        val maxX = (contentSize.width * (scale - 1)) / 2
        val maxY = (contentSize.height * (scale - 1)) / 2

        offset = Offset(
            x = (offset.x + delta.x).coerceIn(-maxX, maxX),
            y = (offset.y + delta.y).coerceIn(-maxY, maxY)
        )
    }
}