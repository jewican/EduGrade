package com.android.edugrade.data.subject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.models.Subject
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toSubject
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import javax.inject.Inject

class SubjectStorage @Inject constructor(
    private val scoreStorage: ScoreStorage,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) {
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

    fun exportSubjectsToJson(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            onFailure(Exception("User not authenticated"))
            return
        }

        try {
            val subjectsList = _subjects.value ?: emptyList()

            val exportObject = mapOf(
                "metadata" to mapOf(
                    "exportDate" to LocalDateTime.now().toString(),
                    "userId" to userId,
                    "totalSubjects" to subjectsList.size
                ),
                "subjects" to subjectsList.map { it.toMap() }
            )

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(exportObject)

            Log.d(TAG, "Successfully exported ${subjectsList.size} subjects to JSON")
            onSuccess(jsonString)

        } catch (e: Exception) {
            Log.e(TAG, "Error exporting subjects to JSON: ${e.message}")
            onFailure(e)
        }
    }

}