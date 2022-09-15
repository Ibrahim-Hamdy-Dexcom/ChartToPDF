package com.example.charttopdf

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.charttopdf.composables.ApolloChart
import com.example.charttopdf.composables.SetupTable
import com.example.charttopdf.ui.theme.ChartToPdfTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChartToPdfTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())) {
                        ApolloChart()
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            Column {
                                SetupTable()
                            }
                        }
                    }
                }
            }
        }
    }
}

class TrendGraphPDFView(
    context: Context,
) : LinearLayoutCompat(context) {

    init {
        val view = ComposeView(context)
        this.addView(view)
        view.setContent {
            PDFScreen()
        }
    }
}

@Composable
fun PDFScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = "Patient Glucose Report",
            style = MaterialTheme.typography.h4,
            color = Color.DarkGray,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { GraphComponent(it, egvs = generateEGVsList().reversed()) })


    }
}

fun generateEGVsList(): List<EGVData> {
    val egvsList = mutableListOf<EGVData>()
    for (i in 0..10) {
        egvsList.add(
            EGVData(
                time = System.currentTimeMillis() -
                        (TimeUnit.MINUTES.toMillis(20) * i),
                value = Random.nextInt(40, 300)
            )
        )
    }
    return egvsList
}

data class EGVData(
    val time: Long,
    val value: Int
)

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