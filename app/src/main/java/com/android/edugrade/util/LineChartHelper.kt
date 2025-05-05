package com.android.edugrade.util

import android.graphics.Color
import com.android.edugrade.models.GpaSnapshot
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.format.DateTimeFormatter

object LineChartHelper {

    fun setupChart(
        chart: LineChart,
        snapshots: List<GpaSnapshot>,
        chartLabel: String = "GPA Over Time",
    ) {
        if (snapshots.isEmpty()) return

        // this part makes sure that only the latest is shown in days with multiple snapshots
        val sorted = snapshots
            .groupBy { it.dateAdded.toLocalDate() }
            .mapValues { it.value.maxByOrNull { snap -> snap.dateAdded }!! }
            .toSortedMap()
            .values
            .toList()

        val entries = sorted.mapIndexed { index, snapshot ->
            val yValue = snapshot.gpa / 100 * 5
            Entry(index.toFloat(), yValue.toFloat())
        }

        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val xLabels = sorted.map { it.dateAdded.format(formatter) }

        val dataSet = LineDataSet(entries, chartLabel).apply {
            color = Color.rgb(76, 175, 80)
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = Color.BLACK
        }

        dataSet.valueFormatter =
            IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                String.format("%.2f", entry?.y)
            }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            animateX(600)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                labelRotationAngle = -35f
                textColor = Color.DKGRAY
            }

            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 5f
            axisLeft.textColor = Color.DKGRAY
            axisRight.isEnabled = false
            legend.isEnabled = false

            setExtraOffsets(10f, 10f, 10f, 30f)

            invalidate()
        }
    }
}