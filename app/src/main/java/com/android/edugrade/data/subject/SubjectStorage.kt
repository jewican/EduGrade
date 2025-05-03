package com.android.edugrade.data.subject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.edugrade.data.auth.UserRepository
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.models.Subject
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toSubject
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class SubjectStorage(
    private val scoreStorage: ScoreStorage,
    private val userRepository: UserRepository) {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val TAG = "SubjectStorage"

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> = _subjects

    fun addSubject(subject: Subject, onSuccess: (() -> Unit)? = null) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        Log.wtf(TAG, "Attempting to save $subject...")

        val subjectRef = database.child("subjects")
            .child(userId)
            .child(subject.code)

        subjectRef.setValue(subject.toMap())
            .addOnSuccessListener {
                Log.e(TAG, "Subject saved successfully")
                onSuccess?.invoke()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Subject saving error! ${e.message}")
                onSuccess?.invoke()
            }
    }

    fun getSubject(code: String): Subject {
        return _subjects.value?.firstOrNull { it.code == code } ?: Subject()
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
                Log.e(TAG, "Subject deleted successfully")
                scoreStorage.deleteScoresForSubject(code)
                userRepository.calculateGpa()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Subject deletion error! ${e.message}")
            }
    }

    fun recalculateSubject(code: String) {
        val subject = getSubject(code)
        scoreStorage.getScoresOfSubject(code) { scoreList ->
            if (scoreList.isEmpty()) {
                Log.w("SubjectStorage", "No scores found for subject: $code")
            }

            val activitiesMap = scoreList.groupBy { it.assessmentTypeId }
            Log.w("SubjectStorage", "Activities map: $activitiesMap")
            subject.calculateOverallGrade(activitiesMap)
            addSubject(subject) {
                userRepository.calculateGpa()
            }
        }
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
                val loadedSubjects = mutableListOf<Subject>()
                for (subjectSnapshot in snapshot.children) {
                    try {
                        val map = subjectSnapshot.value as Map<String, Any>
                        val subject = map.toSubject()
                        loadedSubjects.add(subject)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing subject: ${e.message}")
                    }
                }
                _subjects.value = loadedSubjects
                Log.e(TAG, "Retrieved subjects: $loadedSubjects")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }


}