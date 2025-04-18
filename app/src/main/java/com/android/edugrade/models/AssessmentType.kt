package com.android.edugrade.models

import android.util.Log

data class AssessmentType(
    var name: String = "",
    var grade: Double = 100.0,
    var weight: Double = 0.0,
    val assessmentTypes: MutableList<AssessmentType> = mutableListOf()
) {
    fun calculateGrade(activityMap: Map<String, List<Score>>): Double {
        if (assessmentTypes.isNotEmpty()) {
            grade = assessmentTypes.sumOf { it.calculateGrade(activityMap) * it.weight }

            Log.wtf("GradeCalculation", "Weighted grade of category [$name]: $grade")
            return grade
        }

        val activities = activityMap[name]

        if (activities == null) {
            Log.w("GradeCalculation", "No activities found for [$name] | Using default grade: $grade")
            return grade
        }

        if (activities.isNotEmpty()) {
            val totalScore = activities.sumOf { it.userScore }
            val totalMaxScore = activities.sumOf { it.totalScore }

            grade = if (totalMaxScore > 0) {
                (totalScore / totalMaxScore) * 100
            } else {
                Log.wtf("GradeCalculation", "TotalMaxScore is zero for [$name]!")
                0.0
            }
        }

        Log.wtf("GradeCalculation", "Final grade for [$name]: $grade")
        return grade
    }
}
