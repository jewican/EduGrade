package com.android.edugrade.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.android.edugrade.R
import com.android.edugrade.models.GpaSnapshot
import com.android.edugrade.models.Subject
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.time.format.DateTimeFormatter

object ChartHelper {

    private var primaryColor: Int = Color.BLUE
    private var onSurfaceColor: Int = Color.BLACK
    private var secondaryColor: Int = Color.CYAN
    private var onSurfaceColorVariant: Int = Color.DKGRAY

    fun initColors(context: Context) {
        primaryColor = ContextCompat.getColor(context, R.color.md_theme_primary)
        onSurfaceColor = ContextCompat.getColor(context, R.color.md_theme_onSurface)
        secondaryColor = ContextCompat.getColor(context, R.color.md_theme_secondary)
        onSurfaceColorVariant = ContextCompat.getColor(context, R.color.md_theme_onSurfaceVariant)
    }

    fun setupGpaLineChart(chart: LineChart, snapshots: List<GpaSnapshot>, chartLabel: String = "GPA Over Time") {
        if (snapshots.isEmpty()) return

//        val sorted = snapshots
//            .groupBy { it.dateAdded.toLocalDate() }
//            .mapValues { it.value.maxByOrNull { snap -> snap.dateAdded }!! }
//            .toSortedMap()
//            .values
//            .toList()

        val sorted = snapshots.sortedBy { it.dateAdded }

        val entries = sorted.mapIndexed { index, snapshot ->
            val yValue = snapshot.gpa / 100 * 5
            Entry(index.toFloat(), yValue.toFloat())
        }

        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val xLabels = sorted.map { snapshot ->
            val datePart = snapshot.dateAdded.format(formatter)
            val subjectCode = snapshot.subjectCode
            "$subjectCode | $datePart"
        }

        val dataSet = LineDataSet(entries, chartLabel).apply {
            color = primaryColor
            setCircleColor(primaryColor)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = onSurfaceColor
            valueTextSize = 12f
        }

        dataSet.valueFormatter = IValueFormatter { _, entry, _, _ ->
            entry?.let {
                val label = "%.2f".format(it.y)
                if (it.x == 0f) "       $label" else label // space for first entry to prevent clipping
            } ?: ""
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
                labelRotationAngle = -45f
                textColor = onSurfaceColor
            }

            axisLeft.axisMinimum = 2f
            axisLeft.axisMaximum = 5f
            axisLeft.textColor = onSurfaceColor
            axisRight.isEnabled = false
            legend.isEnabled = false

            setExtraOffsets(10f, 10f, 10f, 50f)
            invalidate()
        }
    }

    fun setupSubjectGradesBarChart(chart: BarChart, subjects: List<Subject>, chartLabel: String = "Subject Grades") {
        if (subjects.isEmpty()) return

        val filteredSubjects = subjects.filter { it.units > 0 }

        val entries = filteredSubjects.mapIndexed { index, subject ->
            val weightedGrade = (subject.overallGrade / 100 * 5) * subject.units
            BarEntry(index.toFloat(), weightedGrade.toFloat())
        }

        val xLabels = filteredSubjects.map { it.code }

        val dataSet = BarDataSet(entries, chartLabel).apply {
            color = secondaryColor
            valueTextColor = onSurfaceColorVariant
            valueTextSize = 12f
            setDrawValues(true)
        }

        dataSet.valueFormatter = IValueFormatter { _, entry, _, _ ->
            String.format("%.2f", entry?.y)
        }

        chart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            animateY(600)
            setExtraOffsets(10f, 10f, 10f, 30f)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                textColor = onSurfaceColorVariant
                labelRotationAngle = -30f
            }

            axisLeft.axisMinimum = 2f
            axisRight.isEnabled = false
            legend.isEnabled = false

            invalidate()
        }
    }
}