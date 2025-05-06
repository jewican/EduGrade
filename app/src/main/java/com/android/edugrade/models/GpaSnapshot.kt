package com.android.edugrade.models

import java.time.LocalDateTime

data class GpaSnapshot(
    val gpa: Double,
    val subjectCode: String,
    val scoreId: String = "",
    val dateAdded: LocalDateTime = LocalDateTime.now(),
)
