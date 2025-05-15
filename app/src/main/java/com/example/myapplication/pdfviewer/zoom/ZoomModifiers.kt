package com.example.myapplication.pdfviewer.zoom

import android.util.Log
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*

fun Modifier.pdfGesture(
    zoomState: ZoomState,
    scrollState: LazyListState,
    coroutineScope: CoroutineScope,
    constraints: Constraints
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
                    zoomState.toggleZoom(tapPosition, constraints)
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
*/

internal fun Modifier.pdfGesture(
    zoomState: ZoomState,
    scrollState: LazyListState,
    constraints: Constraints,
    bitmapScale: Float
): Modifier = composed {

    // 关联到缩放状态，自动控制
    val isScrollEnabled by remember { derivedStateOf { zoomState.scale == zoomState.minScale } }

    this
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { tapPosition ->
                    val minScale = zoomState.minScale
                    zoomState.apply {
                        val targetScale = if (scale > minScale) minScale else 3f
                        val newScale = targetScale.coerceIn(1f, 5f) // 限制缩放范围

                        scale = newScale
                        if (newScale == minScale) {
                            offset = Offset.Zero
                        } else {
                            // 获取视图尺寸（需确保 constraints 已正确传递）
                            val viewWidth = constraints.maxWidth.toFloat()
                            val viewHeight = (constraints.maxWidth / bitmapScale)

                            // 计算理论偏移量
                            val rawOffsetX = (viewWidth / 2 - tapPosition.x) * (newScale - 1)
                            val rawOffsetY = (viewHeight / 2 - tapPosition.y) * (newScale - 1)

                            // 动态约束边界
                            val maxOffsetX = viewWidth * (newScale - 1) / 2
                            val maxOffsetY = viewHeight * (newScale - 1) / 2

                            offset = Offset(
                                x = rawOffsetX.coerceIn(-maxOffsetX, maxOffsetX),
                                y = rawOffsetY.coerceIn(-maxOffsetY, maxOffsetY)
                            )
                        }
                    }
                }
            )
        }
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, _ ->
                zoomState.apply {
                    scale *= zoom
                    scale = scale.coerceIn(zoomState.minScale, zoomState.maxScale)

                    if (scale > zoomState.minScale) {
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(
                                minimumValue = -constraints.maxWidth * (scale - 1) / 2,
                                maximumValue = constraints.maxWidth * (scale - 1) / 2
                            ),
                            y = (offset.y + pan.y).coerceIn(
                                minimumValue = -(constraints.maxWidth / bitmapScale) * (scale - 1) / 2,
                                maximumValue = (constraints.maxWidth / bitmapScale) * (scale - 1) / 2
                            )
                        )
                    } else {
                        //未放大禁止拖动
                        offset = Offset.Zero
                    }
                }
            }
        }
        // 滚动集成
        .scrollable(
            orientation = Orientation.Vertical,
            enabled = isScrollEnabled, // 直接绑定到缩放状态
            state = rememberScrollableState { delta ->
                if (isScrollEnabled) {
                    scrollState.dispatchRawDelta(-delta)
                    delta
                } else 0f
            }
        )
        .graphicsLayer {
            scaleX = zoomState.scale
            scaleY = zoomState.scale
            translationX = zoomState.offset.x
            translationY = zoomState.offset.y
        }
}