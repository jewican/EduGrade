package com.android.edugrade.models

data class Subject(
    val code: String = "-",
    val description: String = "",
    val instructor: String = "",
    val units: Int = 0,
    val assessmentTypes: List<AssessmentType> = listOf(),
    val timeslots: List<Timeslot> = listOf(),
    val overallGrade: Double = 0.0
) {
    fun getLeafNodes(assessments: List<AssessmentType> = assessmentTypes): List<AssessmentType> {
        val leafNodes = mutableListOf<AssessmentType>()
        for (assessment in assessments) {
            if (assessment.assessmentTypes.isEmpty()) {
                leafNodes.add(assessment)
            } else {
                leafNodes.addAll(getLeafNodes(assessment.assessmentTypes))
            }
        }
        return leafNodes
    }
}