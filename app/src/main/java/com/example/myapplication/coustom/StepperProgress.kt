package com.example.myapplication.coustom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SlantedProgressBar(
    progress: Float, // 0f - 1f
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = Color.Blue,
    barHeight: Dp = 8.dp,
    slantHeight: Dp = 12.dp, // 斜角高度
    cornerRadius: Dp = 2.dp,
    animationDuration: Int = 500
) {
    require(progress in 0f..1f) { "Progress must be between 0 and 1" }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = animationDuration),
        label = "progressAnimation"
    )

    Canvas(modifier = modifier.height(barHeight + slantHeight)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barHeightPx = barHeight.toPx()
        val slantHeightPx = slantHeight.toPx()
        val cornerRadiusPx = cornerRadius.toPx()

        // 绘制背景
        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(0f, slantHeightPx),
            size = Size(canvasWidth, barHeightPx),
            cornerRadius = CornerRadius(cornerRadiusPx)
        )

        // 计算进度条长度
        val progressWidth = canvasWidth * animatedProgress

        // 当进度大于0时绘制前景
        if (progressWidth > 0) {
            val path = Path().apply {
                // 主体矩形
                addRoundRect(
                    RoundRect(
                        left = 0f,
                        top = slantHeightPx,
                        right = progressWidth,
                        bottom = slantHeightPx + barHeightPx,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    ))

                    // 斜角部分（当未满100%时显示）
                    if (animatedProgress < 1f) {
                        lineTo(progressWidth, slantHeightPx + barHeightPx)
                        lineTo(progressWidth + slantHeightPx, slantHeightPx)
                        lineTo(progressWidth, slantHeightPx)
                        close()
                    }
            }

            drawPath(
                path = path,
                color = progressColor
            )
        }
    }
}
