package com.android.edugrade.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.edugrade.R
import com.android.edugrade.databinding.ScoreDetailsDialogBinding
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.GpaSnapshot
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.models.Timeslot
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        "id" to id,
        "name" to name,
        "grade" to grade,
        "weight" to weight,
        "assessmentTypes" to assessmentTypes.map { it.toMap() }
    )
}

fun Map<String, Any>.toAssessmentType(): AssessmentType {
    return AssessmentType(
        id = this["id"] as String,
        name = this["name"] as String,
        grade = (this["grade"] as Number).toDouble(),
        weight = (this["weight"] as Number).toDouble(),
        assessmentTypes = ((this["assessmentTypes"] as? List<Map<String, Any>>)
            ?.map { it.toAssessmentType() })?.toMutableList()
            ?: mutableListOf()
    )
}

fun Score.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "code" to code,
        "assessmentTypeId" to assessmentTypeId,
        "name" to name,
        "userScore" to userScore,
        "totalScore" to totalScore,
        "dateAdded" to dateAdded.toString()
    )
}

fun Map<String, Any>.toScore(): Score {
    val userScore = when (val value = this["userScore"]) {
        is Double -> value
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        else -> 0.0
    }

    val totalScore = when (val value = this["totalScore"]) {
        is Double -> value
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        else -> 0.0
    }

    val dateAdded = LocalDateTime.parse(this["dateAdded"] as String)

    return Score(
        id = this["id"] as String,
        code = this["code"] as String,
        assessmentTypeId = this["assessmentTypeId"] as String,
        name = this["name"] as String,
        userScore = userScore,
        totalScore = totalScore,
        dateAdded = dateAdded
    )
}


fun GpaSnapshot.toMap(): Map<String, Any> {
    return mapOf(
        "gpa" to gpa,
        "subjectCode" to subjectCode,
        "scoreId" to scoreId,
        "dateAdded" to dateAdded.toString()
    )
}

fun Map<String, Any>.toGpaSnapshot(): GpaSnapshot {
    val dateAdded = LocalDateTime.parse(this["dateAdded"] as String)

    val gpaValue = when (val gpaRaw = this["gpa"]) {
        is Double -> gpaRaw
        is Long -> gpaRaw.toDouble()
        is Int -> gpaRaw.toDouble()
        else -> throw IllegalArgumentException("Invalid GPA type: $gpaRaw")
    }

    return GpaSnapshot(
        gpa = gpaValue,
        subjectCode = this["subjectCode"] as String,
        scoreId = this["scoreId"] as String,
        dateAdded = dateAdded
    )
}


fun Activity.showDialog(message: String, onDismiss: (() -> Unit)? = null) {
    val dialog = AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton("Ok") { _, _ -> }
        .setOnDismissListener { onDismiss?.invoke() }
        .create()

    dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_surface)
    dialog.show()
}

fun Fragment.showDialog(message: String) {
    val dialog = AlertDialog.Builder(requireContext())
        .setMessage(message)
        .setPositiveButton("Ok") { _, _ -> }
        .create()

    dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_surface)
    dialog.show()
}

fun FragmentActivity.setCurrentFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
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

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.saveToFile(context: Context, filename: String = "scores_export.json"): Uri? {
    return try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(this.toByteArray())
            }
        }

        uri
    } catch (e: Exception) {
        Log.e("StringSaveToFile", "Error saving JSON to file: ${e.message}")
        null
    }
}

fun LocalDateTime.format(): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy | h:mm a", Locale.getDefault())
    return this.format(formatter)
}