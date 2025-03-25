package com.android.edugrade.models

import java.time.LocalTime

data class Subject(
    val code: String = "-",
    val description: String = "",
    val instructor: String = "",
    val units: Int = 0,
    val assessmentTypes: List<AssessmentType> = listOf(),
    val timeslots: List<Timeslot> = listOf(),
    val overallGrade: Double = 0.0
)

data class Timeslot(
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class AssessmentType(
    val name: String = "",
    val grade: Double = 0.0,
    val weight: Double = 0.0
)