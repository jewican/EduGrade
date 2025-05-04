package com.android.edugrade.models

import java.time.LocalDateTime

data class GpaSnapshot(
    val gpa: Double,
    val scoreId: String,
    val dateAdded: LocalDateTime,
)
