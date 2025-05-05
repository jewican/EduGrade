package com.android.edugrade.models

data class BarChartData(
    val subjectS: List<Subject> = listOf(),
    val grades: List<Float>,
    val title: String
)