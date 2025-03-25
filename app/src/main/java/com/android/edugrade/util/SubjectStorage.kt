package com.android.edugrade.util

import android.content.Context
import android.util.Log
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Subject
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException

class SubjectStorage(private val context: Context) {
    private lateinit var subjects: MutableList<Subject>
    private val gson = Converters.registerLocalTime(GsonBuilder()).create();
    private val filename = "subjects.json"

    init {
        loadSubjects()
    }

    fun addSubject(subject: Subject) {
        val existingIndex = subjects.indexOfFirst { it.code == subject.code }

        if (existingIndex != -1) {
            Log.e("SubjectStorage", "Subject already exists! Overwriting details.")
            subjects[existingIndex] = subjects[existingIndex].copy(
                description = subject.description,
                instructor = subject.instructor,
                timeslots = subject.timeslots,
                assessmentTypes = subject.assessmentTypes
            )
        } else {
            Log.e("SubjectStorage", "Subject not found. Adding new subject.")
            subjects.add(subject)
        }

        saveSubjects()
    }

    fun getSubject(code: String): Subject {
        for (subject in subjects)  {
            if (subject.code == code) {
                return subject
            }
        }
        return Subject()
    }

    fun deleteSubject(code: String) {
        val index = subjects.indexOfFirst { it.code == code }
        if (index == -1) {
            Log.e("SubjectStorage", "Subject to delete [${code}] not found!")
            return
        }
        Log.e("SubjectStorage", "[${code}] was deleted.")
        subjects.removeAt(index)
        saveSubjects()
    }

    fun getSubjects() = subjects

    fun loadSubjects() {
        try {
            val jsonString = context.openFileInput(filename).bufferedReader().use {
                it.readText()
            }
            val type = object : TypeToken<List<Subject>>() {}.type
            subjects = gson.fromJson(jsonString, type)
        } catch (e: FileNotFoundException) {
            // hardcoded subjects for testing
            saveHardcodedSubjects(
                listOf(
                    Subject(
                        "CSIT284",
                        "Platform-based Development (Mobile)",
                        "Joemarie Amparo",
                        1,
                        listOf(
                            AssessmentType(
                                "Lab",
                                0.0,
                                0.5
                            ),
                            AssessmentType(
                                "Lecture",
                                0.0,
                                0.5
                            )
                        ),
                        listOf(),
                        4.0
                    )
                )
            )
            loadSubjects()
        }
    }

    private fun saveSubjects() {
        val jsonString = gson.toJson(subjects)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    private fun saveHardcodedSubjects(subjects: List<Subject>) {
        val jsonString = gson.toJson(subjects)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }
}