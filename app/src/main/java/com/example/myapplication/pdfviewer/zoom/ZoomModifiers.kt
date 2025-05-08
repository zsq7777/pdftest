package com.example.myapplication.pdfviewer.zoom

// PdfGestureModifier.kt
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var isScrollEnabled by remember { mutableStateOf(true) }

    this
        // 缩放手势处理
        .pointerInput(zoomState) {
            detectTransformGestures(
                panZoomLock = zoomState.isZooming
            ) { centroid, pan, zoom, _ ->
                zoomState.apply {
                    applyScaleDelta(centroid, zoom)
                    adjustOffset(pan)
                    isScrollEnabled = scale == minScale
                }
            }
        }
        // 双击手势处理
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { tapPosition ->
                    zoomState.toggleZoom()
                    isScrollEnabled = zoomState.scale == zoomState.minScale
                }
            )
        }
        // 滚动集成
        .scrollable(
            orientation = Orientation.Vertical,
            enabled = isScrollEnabled,
            state = rememberScrollableState { delta ->
                if (isScrollEnabled) {
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta * 2) // 调整滚动灵敏度
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