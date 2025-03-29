package com.android.edugrade.util

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.edugrade.R
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.models.Timeslot
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

// Firebase adapters
fun Subject.toMap(): Map<String, Any> {
    return mapOf(
        "code" to code,
        "description" to description,
        "instructor" to instructor,
        "units" to units,
        "assessmentTypes" to assessmentTypes.map { it.toMap() },
        "timeslots" to timeslots.map { it.toMap() },
        "overallGrade" to overallGrade
    )
}

fun Map<String, Any>.toSubject(): Subject {
    val timeslotList = (this["timeslots"] as List<Map<String, Any>>)
        .map { it.toTimeslot() }

    val assessmentList = (this["assessmentTypes"] as List<Map<String, Any>>)
        .map { it.toAssessmentType() }

    return Subject(
        code = this["code"] as String,
        description = this["description"] as String,
        instructor = this["instructor"] as String,
        units = (this["units"] as Number).toInt(),
        assessmentTypes = assessmentList,
        timeslots = timeslotList,
        overallGrade = (this["overallGrade"] as Number).toDouble()
    )
}

fun Timeslot.toMap(): Map<String, Any> {
    return mapOf(
        "type" to type,
        "dayOfWeek" to dayOfWeek.name,
        "startTime" to startTime.toString(),
        "endTime" to endTime.toString()
    )
}

fun Map<String, Any>.toTimeslot(): Timeslot {
    return Timeslot(
        type = this["type"] as String,
        dayOfWeek = DayOfWeek.valueOf(this["dayOfWeek"] as String),  // Convert String back to DayOfWeek
        startTime = LocalTime.parse(this["startTime"] as String),    // Convert String back to LocalTime
        endTime = LocalTime.parse(this["endTime"] as String)
    )
}

fun AssessmentType.toMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "grade" to grade,
        "weight" to weight
    )
}

fun Map<String, Any>.toAssessmentType(): AssessmentType {
    return AssessmentType(
        name = this["name"] as String,
        grade = (this["grade"] as Number).toDouble(),
        weight = (this["weight"] as Number).toDouble()
    )
}

fun Score.toMap(): Map<String, Any> {
    return mapOf(
        "code" to code,
        "assessmentType" to assessmentType,
        "name" to name,
        "userScore" to userScore,
        "totalScore" to totalScore,
        "dateAdded" to dateAdded.toString()
    )
}

fun Map<String, Any>.toScore(): Score {
    val userScore = (this["userScore"] as Long).toDouble()
    val totalScore = (this["totalScore"] as Long).toDouble()
    val dateAdded = LocalDateTime.parse(this["dateAdded"] as String)

    return Score(
        this["code"] as String,
        this["assessmentType"] as String,
        this["name"] as String,
        userScore,
        totalScore,
        dateAdded
    )
}

fun Activity.showError(message: String) {
    AlertDialog.Builder(this).apply {
        setMessage(message)
            .setPositiveButton("Ok") { _, _ -> }
        create()
    }.show()
}

fun Fragment.showError(message: String) {
    AlertDialog.Builder(context).apply {
        setMessage(message)
            .setPositiveButton("Ok") { _, _ -> }
        create()
    }.show()
}

fun FragmentActivity.setCurrentFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        replace(R.id.screenFragment, fragment)
        commit()
    }
}

fun Fragment.setCurrentFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        replace(R.id.screenFragment, fragment)
        addToBackStack("")
        commit()
    }
}