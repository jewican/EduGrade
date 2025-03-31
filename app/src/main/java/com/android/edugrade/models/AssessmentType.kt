package com.android.edugrade.models

data class AssessmentType(
    var name: String = "",
    var grade: Double = 0.0,
    var weight: Double = 0.0,
    val assessmentTypes: MutableList<AssessmentType> = mutableListOf()
)
