package com.android.edugrade.data.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.android.edugrade.data.subject.SubjectStorage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import dagger.Lazy
import kotlinx.coroutines.tasks.await

class UserRepository(private val subjectStorage: Lazy<SubjectStorage>) {
    private val TAG = "UserRepository"
    private val auth: FirebaseAuth = Firebase.auth
    private val usersRef = Firebase.database.reference.child("users")
    private var user: FirebaseUser? = null

    fun currentUser() = user

    suspend fun getCurrentGpa(): Double {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return 0.0
        }

        return try {
            val data = usersRef.child(user!!.uid).child("currentGpa").get().await()
            when (val value = data.value) {
                is Double -> value
                is Long -> value.toDouble()
                else -> 0.0
            }.also { Log.w(TAG, "User's current GPA: $it") }
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

    /**
     * Observes subjects once and calculates GPA.
     * The observer is removed immediately after receiving the data.
     */
    fun calculateGpa() {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return
        }

        val subjectLiveData: LiveData<List<com.android.edugrade.models.Subject>> =
            subjectStorage.get().subjects

        val observer = object : Observer<List<com.android.edugrade.models.Subject>> {
            override fun onChanged(subjects: List<com.android.edugrade.models.Subject>) {
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

                usersRef.child(user!!.uid).child("currentGpa").setValue(finalGpa)
            }
        }

        subjectLiveData.observeForever(observer)
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
