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
    fun toggleZoom(tapPosition: Offset? = null) {
        scale = if (scale > minScale) minScale else 2.5f
        if (scale == minScale) {
            offset = Offset.Zero
        } else {
            // 双击时基于点击位置居中
            tapPosition?.let { pos ->
                offset = Offset(
                    x = pos.x * (1 - scale) / scale,
                    y = pos.y * (1 - scale) / scale
                )
            }
        }
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

    //  边界处理方法
    fun adjustOffset(delta: Offset) {
        if (!isZooming) return

        // 计算实际可移动范围（考虑缩放后的尺寸）
        val scaledWidth = contentSize.width * scale
        val scaledHeight = contentSize.height * scale

        val maxX = (scaledWidth - contentSize.width) / 2
        val maxY = (scaledHeight - contentSize.height) / 2

        offset = Offset(
            x = (offset.x + delta.x).coerceIn(-maxX, maxX),
            y = (offset.y + delta.y).coerceIn(-maxY, maxY)
        )
    }
}