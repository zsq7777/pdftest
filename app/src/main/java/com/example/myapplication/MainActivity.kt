package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.pdfviewer.PdfViewer
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
    val pdfState = rememberPdfViewerState()
    Box (Modifier){
        PdfViewer(
            data = fromAssets("250219.pdf"),
            state = pdfState,
            modifier = Modifier.fillMaxSize()
        )

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
    }
}