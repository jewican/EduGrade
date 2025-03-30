package com.android.edugrade.schema2

import org.junit.Test

data class Subject(
    val code: String,
    val description: String,
    val instructor: String,
    val units: Int,
    val gradeTree: List<GradeNode>
)

data class GradeNode(
    val name: String,
    val weight: Double,
    val categories: List<GradeNode> = listOf()
)

data class Score(
    val name: String,
    val categoryId: String,
    val userScore: Double,
    val totalScore: Double
)

fun calculateOverallGrade(subject: Subject, activityMap: Map<String, List<Score>>): Double {
    return subject.gradeTree.sumOf {
        calculateGrade(it, activityMap) * it.weight
    }
}


fun calculateGrade(
    node: GradeNode,
    activityMap: Map<String, List<Score>>
): Double {
    if (node.categories.isNotEmpty()) {
        val childGrade = node.categories.sumOf {
            calculateGrade(it, activityMap) * it.weight
        }
        return childGrade
    }

    val activities = activityMap[node.name]
        ?: throw IllegalArgumentException("ERROR: ${node.name} was not found in activities map!")

    if (activities.isNotEmpty()) {
        val totalScore = activities.sumOf { it.userScore }
        val totalMaxScore = activities.sumOf { it.totalScore }
        println("Weighted grade of category [${node.name}]: ${((totalScore / totalMaxScore) * 100) * node.weight}")
        return if (totalMaxScore > 0) (totalScore / totalMaxScore) * 100 else 0.0
    }

    return 0.0
}

fun mapActivitiesByCategory(activities: List<Score>): Map<String, List<Score>> {
    return activities.groupBy { it.categoryId }
}

class Schema2UnitTest {
    @Test
    fun calculationTest() {
        val subject = 
            Subject(
                code = "CSIT221",
                description = "Data Structures and Algorithms",
                instructor = "Jay Vince Serato",
                units = 3,
                gradeTree = 
                    listOf(
                        GradeNode(
                            name = "Formative Assessment",
                            weight = 0.525,
                            categories = 
                                listOf(
                                    GradeNode(
                                        name = "Attendance",
                                        weight = 0.07,
                                    ),
                                    GradeNode(
                                        name = "Quizzes",
                                        weight = 0.35
                                    ),
                                    GradeNode(
                                        name = "Group Activities",
                                        weight = 0.03
                                    ),
                                    GradeNode(
                                        name = "Laboratory Activities",
                                        weight = 0.57
                                    )
                                )
                        ),
                        GradeNode(
                            name = "Summative Assessment",
                            weight = 0.475,
                            categories = 
                                listOf(
                                    GradeNode(
                                        name = "Pre-final Exam",
                                        weight = 0.3
                                    ),
                                    GradeNode(
                                        name = "Final Written Exam",
                                        weight = 0.3
                                    ),
                                    GradeNode(
                                        name = "Final Practical Exam",
                                        weight = 0.4
                                    ),
                                    GradeNode(
                                        name = "Bundles of Joy",
                                        weight = 0.05
                                    )
                                )
                        )
                    )
            )
        
        val scores =
            listOf(
                Score(
                    name = "Attendance",
                    categoryId = "Attendance",
                    userScore = 10.0,
                    totalScore = 10.0
                ),
                Score(
                    name = "Trees | Oct25",
                    categoryId = "Quizzes",
                    userScore = 19.0,
                    totalScore = 20.0
                ),
                Score(
                    name = "BST | Nov15",
                    categoryId = "Quizzes",
                    userScore = 17.0,
                    totalScore = 20.0
                ),
                Score(
                    name = "ExpTree | Nov09",
                    categoryId = "Quizzes",
                    userScore = 15.0,
                    totalScore = 15.0
                ),
                Score(
                    name = "DeQue | Oct18",
                    categoryId = "Quizzes",
                    userScore = 10.0,
                    totalScore = 13.0
                ),
                Score(
                    name = "Heaps | Nov22",
                    categoryId = "Quizzes",
                    userScore = 10.0,
                    totalScore = 10.0
                ),
                Score(
                    name = "Graphs",
                    categoryId = "Quizzes",
                    userScore = 8.0,
                    totalScore = 8.0
                ),
                Score(
                    name = "GA3",
                    categoryId = "Group Activities",
                    userScore = 9.0,
                    totalScore = 10.0
                ),
                Score(
                    name = "LA10 Stack | Oct15",
                    categoryId = "Laboratory Activities",
                    userScore = 250.0,
                    totalScore = 250.0
                ),
                Score(
                    name = "LA11",
                    categoryId = "Laboratory Activities",
                    userScore = 250.0,
                    totalScore = 250.0
                ),
                Score(
                    name = "LA12",
                    categoryId = "Laboratory Activities",
                    userScore = 220.0,
                    totalScore = 220.0
                ),
                Score(
                    name = "LA13",
                    categoryId = "Laboratory Activities",
                    userScore = 155.0,
                    totalScore = 155.0
                ),
                Score(
                    name = "LA14",
                    categoryId = "Laboratory Activities",
                    userScore = 155.0,
                    totalScore = 155.0
                ),
                Score(
                    name = "LA15",
                    categoryId = "Laboratory Activities",
                    userScore = 150.0,
                    totalScore = 150.0
                ),
                Score(
                    name = "LA16",
                    categoryId = "Laboratory Activities",
                    userScore = 100.0,
                    totalScore = 100.0
                ), 
                Score(
                    name = "Pre-final Exam",
                    categoryId = "Pre-final Exam",
                    userScore = 92.0,
                    totalScore = 106.0
                ),
                Score(
                    name = "Final Written Exam",
                    categoryId = "Final Written Exam",
                    userScore = 92.0,
                    totalScore = 101.0
                ),
                Score(
                    name = "Final Practical Exam",
                    categoryId = "Final Practical Exam",
                    userScore = 200.0,
                    totalScore = 200.0
                ),
                Score(
                    name = "Bundles of Joy",
                    categoryId = "Bundles of Joy",
                    userScore = 5.0,
                    totalScore = 5.0
                )
            )

        val activityMap = mapActivitiesByCategory(scores)

        println()
        val finalGrade = calculateOverallGrade(subject, activityMap)
        println("------------------------------------------------------------------------------------------------------")
        println("Final Grade: $finalGrade || ${(finalGrade / 100) * 5}")
        println("------------------------------------------------------------------------------------------------------")
    }
}