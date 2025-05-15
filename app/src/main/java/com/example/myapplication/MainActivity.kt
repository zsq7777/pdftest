package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.pdfviewer.PdfViewer
import com.example.myapplication.pdfviewer.PdfViewerState
import com.example.myapplication.pdfviewer.internal.PdfControllerImpl
import com.example.myapplication.pdfviewer.internal.PdfRendererHelper
import com.example.myapplication.pdfviewer.internal.fromAssets
import com.example.myapplication.pdfviewer.rememberPdfViewerState
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Greeting()
            }
        }
    }
}


@Composable
fun Greeting() {
    // 简单用法

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state: PdfViewerState = rememberPdfViewerState()
    // 使用rememberUpdatedState处理数据更新
    val currentData by rememberUpdatedState(fromAssets("250219.pdf"))
    val controller = remember {
        PdfControllerImpl(context, currentData, state, coroutineScope)
    }

    DisposableEffect(controller) {
        onDispose { controller.close() }
    }

    Column(Modifier.padding(top = 60.dp)) {
        Row {
            Text("切换文件A", Modifier.clickable {
                controller.reload(fromAssets("250219.pdf"))
            })
            Text("切换文件B", Modifier.clickable {
                controller.reload(fromAssets("Kotlin.pdf"))
            })
        }
        Row {
            Text("当前页码${controller.currentPage}")
            Text("跳首页", Modifier.clickable {
                controller.jumpToPage(0)
            })
            Text("跳尾页", Modifier.clickable {
                controller.jumpToPage(controller.pageCount - 1)
            })
        }

        PdfViewer(
            controller,
            modifier = Modifier.fillMaxSize(),
            state,

            )

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
    }
}