package com.android.edugrade.util

import android.content.Context
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Subject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException
import javax.inject.Singleton

class SubjectStorage(private val context: Context) {
    private lateinit var subjects: MutableList<Subject>
    private val gson = Gson()
    private val filename = "subjects.json"

    init {
        loadSubjects()
    }

    fun getSubject(code: String): Subject {
        for (subject in subjects)  {
            if (subject.code == code) {
                return subject
            }
        }
        return Subject()
    }

    fun getSubjects() = subjects

    fun saveSubjects() {
        val jsonString = gson.toJson(subjects)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun saveHardcodedSubjects(subjects: List<Subject>) {
        val jsonString = gson.toJson(subjects)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

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
                                4.0,
                                0.5
                            ),
                            AssessmentType(
                                "Lecture",
                                4.0,
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

    fun addSubject(subject: Subject) {
        subjects.add(subject)
        saveSubjects()
    }
}