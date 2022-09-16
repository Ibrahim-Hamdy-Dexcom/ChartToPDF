package com.example.charttopdf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.charttopdf.composables.EGVData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class GraphComponent @JvmOverloads constructor(
    ctx: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    var egvs: List<EGVData>,
) : ConstraintLayout(ctx, attr, defStyleAttr) {

    private val chart: LineChart

    init {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflate the layout into "this" component
        inflater.inflate(R.layout.line_chart, this)
        chart = findViewById(R.id.chart)
        updateLineChart()
    }

    private fun updateLineChart() {
        //SetData
        val chartEGVs = mutableListOf<Entry>()
        egvs.forEachIndexed { index, egvData ->
            chartEGVs.add(Entry(index.toFloat(), egvData.value.toFloat()))
        }
        val dataSet = LineDataSet(chartEGVs, "EGVs")
        dataSet.color = Color.BLACK
        dataSet.setDrawCircles(false)
        chart.data = LineData(dataSet)

        //style the chart
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = XAxisLabelFormatter(egvs)

        val yAxis = chart.axisLeft
        yAxis.setDrawLabels(false)
    }
}

class XAxisLabelFormatter(private val egvs: List<EGVData>) : ValueFormatter() {
    private val mDataFormat = SimpleDateFormat("ha", Locale.US)

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val time: Long = egvs[value.toInt()].time
        return mDataFormat.format(time)
    }
}
