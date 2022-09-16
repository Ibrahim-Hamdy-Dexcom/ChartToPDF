package com.example.charttopdf.composables

import android.content.Context
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.example.charttopdf.CreateSpecificTypeDocument
import com.example.charttopdf.generatePDF
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.reflect.KProperty


@Preview
@Composable
private fun PreviewTable() {
    val propertyNameMappings = listOf(
        "TimeInRange", "NumReadings", "Min", "Max", "Median"
    )
    Column(modifier = Modifier.fillMaxSize()) {
        SetupTable(propertyNameMappings = propertyNameMappings)
    }

}

data class TableRecord(
    val TimeInRange: Float,
    val NumReadings: Int,
    val Min: Int,
    val Max: Int,
    val Median: Int
)

private fun generateRecords(num: Int): List<TableRecord> {
    return (0..num).map {
        TableRecord(
            TimeInRange = Random.nextFloat(),
            NumReadings = Random.nextInt(200),
            Min = Random.nextInt(20),
            Max = Random.nextInt(180, 200),
            Median = Random.nextInt(80, 120)
        )
    }
}

@Composable
fun ApolloTable() {
    val context = LocalContext.current
    var pdfView = remember {
        mutableStateOf<View?>(null)
    }
    val createDocument = rememberLauncherForActivityResult(
        contract = CreateSpecificTypeDocument("application/pdf"),
        onResult = {
            it?.let {
                generatePDF(
                    context = context,
                    uri = it,
                    pdfView = pdfView.value
                ) {
                    Log.e("Dan", "Pdf Generated!")
                }
            }
        })
    AndroidView(factory = {
        TablePDFView(it).apply {
            post {
                pdfView.value = this
            }
        }
    })

    Button(modifier = Modifier.padding(top = 8.dp),
        onClick = { createDocument.launch("Patient_CGM_Report") }) {
        Text(text = "GeneratePdf")
    }
}


class TablePDFView(
    context: Context
) : LinearLayoutCompat(context) {
    init {
        val view = ComposeView(context)
        addView(view)
        view.setContent {
            SetupTableReflection()
        }
        view.apply { ViewCompat.setNestedScrollingEnabled(this, true) }
    }
}


/**
 * Separate into separate function so Preview works because of reflection
 */
@Composable
private fun SetupTableReflection() {
    //TODO add ability to do icons into cells?
    //Will use for row names
    val propertyNameMappings = //Add new mapping for property to a name
        TableRecord::class.members.mapNotNull {
            if (it is KProperty) {
                //Add new mapping for property to a name
                if (it == TableRecord::NumReadings) {
                    "Number Readings"
                } else {
                    it.name
                }
            } else {
                null
            }
        }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            SetupTable(propertyNameMappings = propertyNameMappings)
        }
    }


}

/**
 * Finish data structures setup for table
 */
@Composable
private fun SetupTable(propertyNameMappings: List<String>) {

    val sdf = SimpleDateFormat("ha")
    //Will use for col headers
    val hourlyIntervalHeaders = (0..12).toList().map {
        val hour = it.toLong()
        val begin = TimeUnit.HOURS.toMillis(hour)
        val end = TimeUnit.HOURS.toMillis(hour + 1)
        "${sdf.format(begin)} - ${sdf.format(end)}"
    }

    val records = generateRecords(12)
    Table(
        descriptionLabels = hourlyIntervalHeaders,
        propertyLabels = propertyNameMappings,
        records
    )
}

/**
 * Table composable
 */
@Composable
private fun Table(
    descriptionLabels: List<String>,
    propertyLabels: List<String>,
    records: List<TableRecord>
) {

    //In this case the description labels with be the Col headers
    //The Row labels will be the properties in TableRecord
    for (rowIndex in 0..propertyLabels.size) {
        Row {
            for (colIndex in 0..descriptionLabels.size) {

                Column {
                    if (colIndex == 0 && rowIndex == 0) {
                        RowHeaderText(text = "")
                    } else if (rowIndex == 0) {//Col 1..n
                        //Column headers
                        ColHeaderText(text = descriptionLabels[colIndex - 1])
                    } else if (colIndex == 0) {//Row 1..n
                        //Row start headers
                        RowHeaderText(text = propertyLabels[rowIndex - 1])
                    } else {
                        val record = records[rowIndex - 1]
                        val recordLabel = propertyLabels[rowIndex - 1]
                        CellText(text = "Cell value")
                    }
                }

            }
        }
    }

}

val cellWidth = 60.dp
val cellHeight = 60.dp

@Composable
private fun ColHeaderText(text: String) {
    Text(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Cyan)
            .size(cellWidth, 50.dp),
        text = text
    )
}

@Composable
private fun RowHeaderText(text: String) {

    Text(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Yellow)
            .size(120.dp, 50.dp),
        text = text
    )
}


@Composable
private fun CellText(text: String) {
    Text(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Green)
            .size(cellWidth, cellHeight),
        text = text
    )
}

