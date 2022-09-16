package com.example.charttopdf

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.charttopdf.composables.TablePDFView
import com.example.charttopdf.composables.TrendGraphPDFView
import com.example.charttopdf.ui.theme.ChartToPdfTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    val sdf = SimpleDateFormat("h:mm:ss_a")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        setContent {
            val context = LocalContext.current
            var chartPdfView = remember {
                mutableStateOf<View?>(null)
            }
            var tablePDFView = remember {
                mutableStateOf<View?>(null)
            }
            val chartDocument = rememberLauncherForActivityResult(
                contract = CreateSpecificTypeDocument("application/pdf"),
                onResult = {
                    it?.let {
                        generatePDF(
                            context = context,
                            uri = it,
                            pdfView = chartPdfView.value
                        ) {
                            Log.e("Ibrahim", "Pdf Generated!")
                        }
                    }
                })
            val tableDocument = rememberLauncherForActivityResult(
                    contract = CreateSpecificTypeDocument("application/pdf"),
            onResult = {
                it?.let {
                    generatePDF(
                        context = context,
                        uri = it,
                        pdfView = tablePDFView.value
                    ) {
                        Log.e("Ibrahim", "Pdf Generated!")
                    }
                }
            })

            ChartToPdfTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AndroidView(factory = {
                            TrendGraphPDFView(it).apply {
                                post {
                                    chartPdfView.value = this
                                }
                            }
                        })
                        Button(modifier = Modifier.padding(top = 8.dp),
                            onClick = {
                                chartDocument.launch("Chart.${sdf.format(System.currentTimeMillis())}")
                            }) {
                            Text(text = "GeneratePdf")
                        }
                        AndroidView(factory = {
                            TablePDFView(it).apply {
                                post {
                                    tablePDFView.value = this
                                }
                            }
                        })
                        Button(modifier = Modifier.padding(top = 8.dp),
                            onClick = {
                                tableDocument.launch("Table.${sdf.format(System.currentTimeMillis())}")
                            }) {
                            Text(text = "GeneratePdf")
                        }

                    }
                }
            }
        }
    }
}
fun generatePDF(context: Context, uri: Uri, pdfView: View?, callback: () -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        pdfView?.let {
            val bitmap = PDFHandler.createBitmapFromView(it)
            PDFHandler.convertBitMapToPDF(
                context = context,
                uri = uri,
                bitmap = bitmap,
                callback = callback
            )
        }
    }
