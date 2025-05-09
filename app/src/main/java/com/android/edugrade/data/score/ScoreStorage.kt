package com.android.edugrade.data.score

import android.util.Log
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.models.Score
import com.android.edugrade.util.toMap
import com.android.edugrade.util.toScore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import dagger.Lazy
import java.time.LocalDateTime
import javax.inject.Inject

class ScoreStorage @Inject constructor(
    private val userRepository: UserRepository,
    private val subjectStorage: Lazy<SubjectStorage>,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) {
    private var scores: MutableList<Score> = mutableListOf()
    private val TAG = "ScoreStorage"

    fun addScore(
        score: Score,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        Log.wtf(TAG, "Attempting to save ${score.name}...")

        val scoreRef = database.child("scores") // check if score exists (updating)
            .child(userId)
            .child(score.id)

        scoreRef.get().addOnSuccessListener { dataSnapshot ->
            val isUpdate = dataSnapshot.exists()

            scoreRef.setValue(score.toMap())
                .addOnSuccessListener {
                    if (isUpdate) {
                        userRepository.deleteSnapshotsAfterScore(score.id) // delete snapshots if updating a score
                        userRepository.calculateGpa()
                        Log.e(TAG, "Score updated and subsequent GPA snapshots deleted")
                    } else {
                        userRepository.calculateGpa(score.code, score.id)
                        Log.e(TAG, "New score saved with GPA snapshot")
                    }
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Score saving error! ${e.message}")
                    onFailure(e)
                }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error checking if score exists: ${e.message}")
            onFailure(e)
        }
    }

    fun getScore(scoreId: String): Score? {
        return scores.find { it.id == scoreId }
    }

    fun deleteScore(
        scoreId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "User is not authenticated!")
            onFailure(Exception("User not authenticated"))
            return
        }

        val scoreRef = database.child("scores").child(userId).child(scoreId)
        val deletedScore = getScore(scoreId)
        if (deletedScore == null) {
            Log.w(TAG, "Score with ID $scoreId not found!")
        } else {
            scores.removeAll { it.id == scoreId }
        }

        scoreRef.removeValue()
            .addOnSuccessListener {
                subjectStorage.get().recalculateSubject(deletedScore!!.code)
                userRepository.deleteSnapshotsAfterScore(scoreId)
                Log.d(TAG, "Score $scoreId successfully deleted.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete score $scoreId: ${e.message}")
                onFailure(e)
            }
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
                        try {
                            val scoreMap = scoreSnapshot.value as Map<String, Any>
                            val score = scoreMap.toScore()
                            scores.add(score)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing score: ${e.message}")
                        }
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

    fun deleteScoresForSubject(subjectCode: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.wtf(TAG, "User is not authenticated!")
            return
        }

        val scoresRef = database.child("scores").child(userId)

        scoresRef.orderByChild("code").equalTo(subjectCode)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun exportScoresToJson(
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
            val exportObject = mapOf(
                "metadata" to mapOf(
                    "exportDate" to LocalDateTime.now().toString(),
                    "userId" to userId,
                    "totalScores" to scores.size
                ),
                "scores" to scores.map { it.toMap() }
            )

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(exportObject)

            Log.d(TAG, "Successfully exported ${scores.size} scores to JSON")
            onSuccess(jsonString)

        } catch (e: Exception) {
            Log.e(TAG, "Error exporting scores to JSON: ${e.message}")
            onFailure(e)
        }
    }
}