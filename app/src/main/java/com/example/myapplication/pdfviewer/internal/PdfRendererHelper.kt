package com.example.myapplication.pdfviewer.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import androidx.core.graphics.createBitmap

fun fromAssets(assetName: String) = PdfRendererHelper.AssetWrapper(assetName)

class PdfRendererHelper(
    private val context: Context,
    private val data: Any
) {
    private var renderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null

    // assets 标识类型
    class AssetWrapper(val assetName: String)

    val pageCount: Int
        get() = renderer?.pageCount ?: 0

    init {
        when (data) {
            is AssetWrapper -> initFromAssets(data.assetName)
            is String -> initWithFile(File(data))
            is File -> initWithFile(data)
            is Uri -> initWithUri(data)
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }


    private fun initFromAssets(assetName: String) {
        val tempFile = createTempFileFromAssets(context, assetName)
        val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(pfd)
        tempFile.deleteOnExit() // 使用后自动删除
    }

    private fun createTempFileFromAssets(context: Context, assetName: String): File {
        val tempFile = File.createTempFile("pdf_", ".tmp", context.cacheDir)
        context.assets.open(assetName).use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun initWithFile(file: File) {
        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(pfd)
    }

    private fun initWithUri(uri: Uri) {
        context.contentResolver.openFileDescriptor(uri, "r")?.let {
            renderer = PdfRenderer(it)
        }
    }

    fun renderPage(pageIndex: Int): Bitmap {
        require(pageIndex in 0 until pageCount)
        currentPage?.close()
        return renderer!!.openPage(pageIndex).use { page ->
            // 1. 获取屏幕密度比例因子
            val displayMetrics = context.resources.displayMetrics
            val scaleFactor = displayMetrics.density // 或自定义缩放逻辑

            // 2. 计算适配屏幕的 Bitmap 尺寸
            val scaledWidth = (page.width * scaleFactor).toInt()
            val scaledHeight = (page.height * scaleFactor).toInt()

            // 3. 创建 Bitmap 并填充白色背景
            val bitmap = createBitmap(
                scaledWidth,
                scaledHeight,
                Bitmap.Config.ARGB_8888
            )
            // 使用 eraseColor 填充白色，防止透明背景
            bitmap.eraseColor(Color.WHITE)

            // 4. 渲染页面到 Bitmap
            page.render(
                bitmap,           // 目标 Bitmap
                null,             // 裁剪区域（null 表示不裁剪）
                null,             // 变换矩阵（null 表示无额外缩放）
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY // 或尝试 RENDER_MODE_FOR_PRINT
            )

            bitmap
        }
    }
    fun close() {
        currentPage?.close()
        renderer?.close()
    }
}