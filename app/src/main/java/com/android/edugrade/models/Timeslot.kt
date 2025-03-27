package com.android.edugrade.models

import java.time.DayOfWeek
import java.time.LocalTime

data class Timeslot(
    val type: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
)
