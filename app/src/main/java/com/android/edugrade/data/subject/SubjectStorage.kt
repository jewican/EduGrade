package com.android.edugrade.data.subject

import android.content.Context
import android.util.Log
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toSubject
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException
import javax.inject.Inject

class SubjectStorage(private val scoreStorage: ScoreStorage) {
    private var subjects: MutableList<Subject> = mutableListOf()
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val TAG = "SubjectStorage"

    fun addSubject(subject: Subject) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        Log.wtf(TAG, "Attempting to save ${subject.code}...")

        val subjectRef = database.child("subjects")
            .child(userId)
            .child(subject.code)

        subjectRef.setValue(subject.toMap())
            .addOnSuccessListener {
                Log.e(TAG, "Subject saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Subject saving error! ${e.message}")
            }
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
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }
        val subjectsRef = database.child("subjects")
            .child(userId)
            .child(code)

        subjectsRef.removeValue()
            .addOnSuccessListener {
                Log.e(TAG, "Subject saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Subject deletion error! ${e.message}")
            }
    }

    fun getSubjects(): List<Subject> = subjects

    fun recalculateSubject(code: String) {
        val subject = getSubject(code)
        scoreStorage.getScores(code) { scoreList ->
            val activitiesMap = scoreList.groupBy { it.assessmentType }
            Log.w("SubjectStorage", "$activitiesMap")
            subject.calculateOverallGrade(activitiesMap)
            addSubject(subject)
        }
    }

    fun getAssessmentTypes(code: String): List<AssessmentType> {
        val index = subjects.indexOfFirst { it.code == code }
        if (index == -1) {
            Log.e("SubjectStorage", "Subject [${code}] not found! Returning empty assessment list")
            return listOf()
        }
        return subjects[index].assessmentTypes
    }

    fun loadSubjects() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            return
        }

        val subjectsRef = database.child("subjects").child(userId)
        Log.e(TAG, "Retrieving subjects...")

        subjectsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e(TAG, "Subject data received! Building subjects")
                subjects = mutableListOf()
                for (subjectSnapshot in snapshot.children) {
                    try {
                        val map = subjectSnapshot.value as Map<String, Any>
                        val subject = map.toSubject()
                        subjects.add(subject)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing subject: ${e.message}")
                    }
                }
                Log.e(TAG, "Retrieved subjects: $subjects")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }

}