package com.android.edugrade.models

import java.time.LocalDateTime
import java.util.UUID

data class Score(
    val id: String = UUID.randomUUID().toString(),
    val code: String,
    val assessmentTypeId: String,
    val name: String,
    val userScore: Double,
    val totalScore: Double,
    val dateAdded: LocalDateTime
)