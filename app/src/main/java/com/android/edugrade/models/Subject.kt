package com.android.edugrade.models

data class Subject(
    val code: String,
    val description: String = "",
    val assessmentTypes: List<AssessmentType>,
    val overallGrade: Double = 0.0
)

data class AssessmentType(
    val name: String,
    val grade: Double,
    val weight: Double
)