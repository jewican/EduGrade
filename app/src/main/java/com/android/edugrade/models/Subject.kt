package com.android.edugrade.models

data class Subject(
    val code: String = "-",
    val description: String = "",
    val instructor: String = "",
    val units: Int = 0,
    val assessmentTypes: List<AssessmentType> = listOf(),
    val timeslots: List<Timeslot> = listOf(),
    val overallGrade: Double = 0.0
)


