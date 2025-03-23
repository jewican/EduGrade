package com.android.edugrade.util

import android.content.Context
import com.android.edugrade.models.Subject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Singleton

class SubjectStorage(private val context: Context) {
    private lateinit var subjects: MutableList<Subject>
    private val gson = Gson()
    private val filename = "subjects.json"

    init {
        loadSubjects()
    }

    fun getSubject(code: String): Subject? {
        for (subject in subjects)  {
            if (subject.code == code) {
                return subject
            }
        }
        return null
    }

    fun getSubjects() = subjects

    fun saveSubjects(subjects: List<Subject>) {
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
        } catch (e: Exception) {
            subjects = mutableListOf()
        }
    }
}