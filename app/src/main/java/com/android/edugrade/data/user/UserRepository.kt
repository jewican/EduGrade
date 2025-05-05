package com.android.edugrade.data.user

import android.util.Log
import androidx.lifecycle.Observer
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.models.GpaSnapshot
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.util.toGpaSnapshot
import com.android.edugrade.util.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.Lazy
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val subjectStorage: Lazy<SubjectStorage>,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) {
    private val TAG = "UserRepository"
    private val usersRef = database.child("users")
    private var user: FirebaseUser? = null

    fun currentUser() = user

    suspend fun getCurrentGpa(): Double {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return 0.0
        }

        return try {
            val data = usersRef.child(user!!.uid).child("currentGpa").get().await()
            val rawGpa = when (val value = data.value) {
                is Double -> value
                is Long -> value.toDouble()
                else -> 0.0
            }

            val fivePointGpa = rawGpa / 100 * 5
            Log.w(TAG, "User's current GPA (raw): $rawGpa")
            Log.w(TAG, "User's current GPA (5-point): $fivePointGpa")

            fivePointGpa
        } catch (e: Exception) {
            Log.w(TAG, "Error getting current GPA! ${e.message}")
            0.0
        }
    }

    suspend fun getTargetGpa(): Double {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return 0.0
        }

        return try {
            val data = usersRef.child(user!!.uid).child("targetGpa").get().await()
            when (val value = data.value) {
                is Double -> value
                is Long -> value.toDouble()
                else -> 0.0
            }.also { Log.w(TAG, "User's target GPA: $it") }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting target GPA! ${e.message}")
            0.0
        }
    }

    fun getGpaHistory(onResult: (List<GpaSnapshot>) -> Unit) {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return
        }

        usersRef
            .child(user!!.uid)
            .child("gpaSnapshots")
            .orderByChild("dateAdded")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val gpaSnapshots = mutableListOf<GpaSnapshot>()
                    for (scoreSnapshot in snapshot.children) {
                        try {
                            val gpaSnapshotMap = scoreSnapshot.value as Map<String, Any>
                            val gpaSnapshot = gpaSnapshotMap.toGpaSnapshot()
                            gpaSnapshots.add(gpaSnapshot)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing score: ${e.message}")
                        }
                    }
                    gpaSnapshots.reverse()
                    Log.w(TAG, "GPA snapshots retrieved: $gpaSnapshots")
                    onResult(gpaSnapshots)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error retrieving GPA snapshots: ${error.message}")
                }
            })
    }

    // only save a snapshot of gpa if a score was added
    fun calculateGpa(scoreId: String? = null) {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return
        }

        val subjectLiveData = subjectStorage.get().subjects

        val observer = object : Observer<List<Subject>> {
            override fun onChanged(subjects: List<Subject>) {
                subjectLiveData.removeObserver(this)

                if (subjects.isEmpty()) {
                    Log.w(TAG, "User has no subjects, skipping GPA calculation")
                    return
                }

                val rawSum = subjects.sumOf { it.overallGrade * it.units }
                val totalUnits = subjects.sumOf { it.units }
                val gpa = rawSum / totalUnits
                val finalGpa = gpa / 100 * 5

                Log.w(TAG, "Calculated GPA of user: $gpa")
                Log.w(TAG, "As 5-point GPA: $finalGpa")

                usersRef.child(user!!.uid).child("currentGpa").setValue(gpa)

                if (scoreId != null) {
                    saveGpaSnapshot(scoreId, gpa)
                }
            }
        }

        subjectLiveData.observeForever(observer)
    }

    fun saveGpaSnapshot(scoreId: String, gpa: Double) {
        val gpaHistoryRef = usersRef.child(user!!.uid).child("gpaSnapshots").push()
        val snapshot = GpaSnapshot(
            gpa = gpa,
            scoreId = scoreId,
            dateAdded = LocalDateTime.now()
        )
        gpaHistoryRef.setValue(snapshot.toMap())
            .addOnSuccessListener {
                Log.w(TAG, "Saved GPA snapshot successfully")
            }
            .addOnFailureListener {
                Log.w(TAG, "GPA snapshot saving error: ${it.message}")
            }
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                usersRef.child(it.uid).apply {
                    child("name").setValue(username)
                    child("email").setValue(email)
                }
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Error creating user! ${e.message}")
            false
        }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                user = auth.currentUser
                Log.w(TAG, "Login successful with user UID ${user?.uid}")
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onFailure.invoke(it)
            }
    }

    fun signOut() {
        auth.signOut()
        user = null
    }
}
