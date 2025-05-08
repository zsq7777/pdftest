package com.example.myapplication.pdfviewer.zoom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.pdfGesture(
    zoomState: ZoomState,
    scrollState: LazyListState,
    coroutineScope: CoroutineScope
): Modifier = composed {
    // 关联到缩放状态，自动控制
    val isScrollEnabled by remember { derivedStateOf { zoomState.scale == zoomState.minScale } }

    this
        // 缩放手势处理
        .pointerInput(zoomState) {
            detectTransformGestures(
                panZoomLock = zoomState.isZooming
            ) { centroid, pan, zoom, _ ->
                zoomState.apply {
                    applyScaleDelta(centroid, zoom)
                    adjustOffset(pan)
                }
            }
        }
        // 双击手势处理
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { tapPosition ->
                    zoomState.toggleZoom(tapPosition)
                }
            )
        }
        // 滚动集成
        .scrollable(
            orientation = Orientation.Vertical,
            enabled = isScrollEnabled, // 直接绑定到缩放状态
            state = rememberScrollableState { delta ->
                if (isScrollEnabled) {
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta) // 移除灵敏度系数
                    }
                    delta
                } else 0f
            }
        )
        // 应用图形变换
        .graphicsLayer {
            scaleX = zoomState.scale
            scaleY = zoomState.scale
            translationX = zoomState.offset.x
            translationY = zoomState.offset.y
        }
}