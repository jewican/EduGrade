package com.android.edugrade.models

import java.time.LocalDateTime

data class Score(
    val code: String,
    val assessmentTypeId: String,
    val name: String,
    val userScore: Double,
    val totalScore: Double,
    val dateAdded: LocalDateTime
)