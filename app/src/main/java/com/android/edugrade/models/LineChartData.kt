package com.android.edugrade.models

data class LineChartData(
    val assessments: List<Pair<String, Float>>,
    val subjectName: String
)

