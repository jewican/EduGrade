package com.android.edugrade

import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class Subject(
    val code: String = "-",
    val description: String = "",
    val instructor: String = "",
    val units: Int = 0,
    val gradeTree: List<GradeNode> = listOf(),
    val timeslots: List<Timeslot> = listOf(),
    val overallGrade: Double = 0.0) {

    fun calculateOverallGrade(): Double {
        return gradeTree.sumOf { it.calculateGrade() * it.weight }
    }

}

class GradeNode(
    val name: String,
    val weight: Double,
    val children: List<GradeNode> = listOf(),
    val activities: List<Score> = listOf(),
    val grade: Double = 0.0) {

    fun calculateGrade(): Double {
        if (children.isNotEmpty()) {
            val childGrade = children.sumOf { it.calculateGrade() * it.weight }
            return childGrade
        }

        if (activities.isNotEmpty()) {
            val totalScore = activities.sumOf { it.userScore }
            val totalMaxScore = activities.sumOf { it.totalScore }
            return if (totalMaxScore > 0) (totalScore / totalMaxScore) * 100 else 0.0
        }

        return 0.0
    }

}

data class Score(
    val name: String,
    val userScore: Double,
    val totalScore: Double
)

data class Timeslot(
    val type: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
)

class ExampleUnitTest {
    @Test
    fun testSubject() {
        val testSub =
            Subject(
                code = "CSIT221",
                description = "Katong DSA nako sa una",
                instructor = "James Ewican",
                units = 3,
                gradeTree =
                    listOf(
                        GradeNode(
                            name = "Formative Assessment",
                            weight = 0.525,
                            children =
                                listOf(
                                    GradeNode(
                                        name = "Attendance",
                                        weight = 0.07,
                                        activities =
                                            listOf(
                                                Score(
                                                    name = "Attendance",
                                                    userScore = 10.0,
                                                    totalScore = 10.0
                                                )
                                            )
                                    ),
                                    GradeNode(
                                        name = "Quizzes",
                                        weight = 0.35,
                                        activities =
                                            listOf(
                                                Score(
                                                    name = "Trees | Oct25",
                                                    userScore = 19.0,
                                                    totalScore = 20.0
                                                ),
                                                Score(
                                                    name = "BST | Nov15",
                                                    userScore = 17.0,
                                                    totalScore = 20.0
                                                ),
                                                Score(
                                                    name = "ExpTree | Nov09",
                                                    userScore = 15.0,
                                                    totalScore = 15.0
                                                ),
                                                Score(
                                                    name = "DeQue | Oct18",
                                                    userScore = 10.0,
                                                    totalScore = 13.0
                                                ),
                                                Score(
                                                    name = "Heaps | Nov22",
                                                    userScore = 10.0,
                                                    totalScore = 10.0
                                                ),
                                                Score(
                                                    name = "Graphs",
                                                    userScore = 8.0,
                                                    totalScore = 8.0
                                                )
                                            )
                                    ),
                                    GradeNode(
                                        name = "Group Activities",
                                        weight = 0.03,
                                        activities =
                                            listOf(
                                                Score(
                                                    name = "GA3",
                                                    userScore = 9.0,
                                                    totalScore = 10.0
                                                )
                                            )
                                    ),
                                    GradeNode(
                                        name = "Laboratory Activities",
                                        weight = 0.57,
                                        activities =
                                            listOf(
                                                Score(
                                                    name = "LA10 Stack | Oct15",
                                                    userScore = 250.0,
                                                    totalScore = 250.0
                                                ),
                                                Score(
                                                    name = "LA11",
                                                    userScore = 250.0,
                                                    totalScore = 250.0
                                                ),
                                                Score(
                                                    name = "LA12",
                                                    userScore = 220.0,
                                                    totalScore = 220.0
                                                ),
                                                Score(
                                                    name = "LA13",
                                                    userScore = 155.0,
                                                    totalScore = 155.0
                                                ),
                                                Score(
                                                    name = "LA14",
                                                    userScore = 155.0,
                                                    totalScore = 155.0
                                                ),
                                                Score(
                                                    name = "LA15",
                                                    userScore = 150.0,
                                                    totalScore = 150.0
                                                ),
                                                Score(
                                                    name = "LA16",
                                                    userScore = 100.0,
                                                    totalScore = 100.0
                                                )
                                            )
                                    )
                                )
                        ),
                        GradeNode(
                            name = "Summative Assessment",
                            weight = 0.475,
                            children =
                            listOf(
                                GradeNode(
                                    name = "Pre-final Exams",
                                    weight = 0.3,
                                    activities =
                                        listOf(
                                            Score(
                                                name = "Pre-final Exam",
                                                userScore = 92.0,
                                                totalScore = 106.0
                                            )
                                        )
                                ),
                                GradeNode(
                                    name = "Final Written Exam",
                                    weight = 0.3,
                                    activities =
                                        listOf(
                                            Score(
                                                name = "Final Written Exam",
                                                userScore = 92.0,
                                                totalScore = 101.0
                                            )
                                        )
                                ),
                                GradeNode(
                                    name = "Final Practical Exam",
                                    weight = 0.4,
                                    activities =
                                        listOf(
                                            Score(
                                                name = "Final Practical Exam",
                                                userScore = 200.0,
                                                totalScore = 200.0
                                            )
                                        )
                                ),
                                GradeNode(
                                    name = "Bundles of Joy",
                                    weight = 0.0,
                                    activities =
                                    listOf(
                                        Score(
                                            name = "Bundles of Joy",
                                            userScore = 1.0,
                                            totalScore = 1.0
                                        )
                                    )
                                )
                            )
                        )
                    )
            )

        println("------------------------------------------------------------------------------------------------------")
        println("Final Grade: ${testSub.calculateOverallGrade()}")
        println("Top-level grade breakdown:")
        var totalGrade: Double = 0.0
        for (node in testSub.gradeTree) {
            println("${node.name}: ${node.calculateGrade() * node.weight} / ${100 * node.weight}")
            totalGrade += node.calculateGrade() * node.weight
        }
        println("Total Grade from sum of top level: $totalGrade")
        println("------------------------------------------------------------------------------------------------------")
    }
}