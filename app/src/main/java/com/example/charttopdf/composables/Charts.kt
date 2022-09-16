package com.example.charttopdf.composables

import android.content.Context
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.charttopdf.GraphComponent
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@Preview
@Composable
private fun PreviewChart() {
    PDFScreen()
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
private fun PDFScreen() {
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

private fun generateEGVsList(): List<EGVData> {
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



