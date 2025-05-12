package com.example.myapplication.pdfviewer.zoom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
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


}