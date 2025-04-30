package com.android.edugrade.data.score

import android.util.Log
import com.android.edugrade.models.Score
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toScore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ScoreStorage {
    private var scores: MutableList<Score> = mutableListOf()
    private val database = Firebase.database.reference
    private val auth = Firebase.auth

    private val TAG = "ScoreStorage"

    fun addScore(
        score: Score,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        Log.wtf(TAG, "Attempting to save ${score.name}...")

        val scoreRef = database.child("scores")
            .child(userId)
            .push()

        scoreRef.setValue(score.toMap())
            .addOnSuccessListener {
                Log.e(TAG, "Score saved successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Score saving error! ${e.message}")
                onFailure(e)
            }
    }

    fun deleteScoresForSubject(subjectCode: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        val scoresRef = database.child("scores").child(userId)

        scoresRef.orderByChild("code").equalTo(subjectCode).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoresToDelete = snapshot.children.map { it.key }.toList()

                if (scoresToDelete.isNotEmpty()) {
                    val deleteTasks = scoresToDelete.map { key ->
                        scoresRef.child(key!!).removeValue()
                    }

                    for (deleteTask in deleteTasks) {
                        deleteTask.addOnSuccessListener {
                            Log.d(TAG, "Score successfully deleted for subject: $subjectCode")
                        }
                        deleteTask.addOnFailureListener { e ->
                            Log.e(TAG, "Error deleting score: ${e.message}")
                        }
                    }
                } else {
                    Log.d(TAG, "No scores found for subject code: $subjectCode")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error querying scores: ${error.message}")
            }
        })
    }

    fun getAllScores(): List<Score> = scores

    fun getScoresOfSubject(code: String, onResult: (List<Score>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            return
        }

        database
            .child("scores")
            .child(userId)
            .orderByChild("code")
            .equalTo(code)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scores = mutableListOf<Score>()
                    for (scoreSnapshot in snapshot.children) {
                        val scoreMap = scoreSnapshot.value as Map<String, Any>
                        val score = scoreMap.toScore()
                        scores.add(score)
                    }
                    scores.reverse()
                    Log.w(TAG, "Scores of $code: $scores")
                    onResult(scores)
                }
                override fun onCancelled(error: DatabaseError) {
                    println("Error: ${error.message}")
                }
            })
    }

    // TODO: limit queries to recent scores only
    fun loadScores() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            return
        }

        val scoresRef = database
            .child("scores")
            .child(userId)
            .orderByChild("dateAdded")

        scoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scores = mutableListOf()
                for (scoreSnapshot in snapshot.children) {
                    try {
                        val map = scoreSnapshot.value as Map<String, Any>
                        val score = map.toScore()
                        scores.add(score)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing score: ${e.message}")
                    }
                }
                scores.reverse()
                Log.e(TAG, "Retrieved scores: $scores")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }
}