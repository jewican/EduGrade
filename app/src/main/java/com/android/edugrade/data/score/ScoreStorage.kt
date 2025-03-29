package com.android.edugrade.data.score

import android.util.Log
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toScore
import com.android.edugrade.util.toSubject
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.gson.GsonBuilder

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
            .child(score.code)
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

    fun getScores(): List<Score> = scores

    fun loadScores() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            return
        }

        val scoresRef = database
            .child("scores")
            .child(userId)

        scoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scores = mutableListOf()
                for (subjectSnapshot in snapshot.children) {
                    for (scoreSnapshot in subjectSnapshot.children) {
                        try {
                            val map = scoreSnapshot.value as Map<String, Any>
                            val score = map.toScore()
                            scores.add(score)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing score: ${e.message}")
                        }
                    }
                }

                Log.e(TAG, "Retrieved scores: $scores")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }
}