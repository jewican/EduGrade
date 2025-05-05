package com.android.edugrade.util

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object LineChartHelper {

    fun setupLineChart(lineChart: LineChart, entries: List<Entry>, label: String) {
        val dataSet = LineDataSet(entries, label).apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 5f
            setDrawValues(false)
        }

        lineChart.data = LineData(dataSet)
        lineChart.description.isEnabled = false
        lineChart.animateX(1000)
        lineChart.invalidate()
    }
}