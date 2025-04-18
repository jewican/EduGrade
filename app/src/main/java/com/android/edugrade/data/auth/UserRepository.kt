package com.android.edugrade.data.auth

import android.util.Log
import com.android.edugrade.data.subject.SubjectStorage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import dagger.Lazy

class UserRepository(private val subjectStorage: Lazy<SubjectStorage>) {
    private val TAG = "UserRepository"
    private var auth: FirebaseAuth = Firebase.auth
    private val usersRef = Firebase.database.reference.child("users")
    private var user: FirebaseUser? = null

    fun currentUser() = user

    suspend fun getCurrentGpa(): Double {
        var currentGpa: Double = 0.0

        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return 0.0
        }

        return try {
            val data = usersRef.child(user!!.uid).child("currentGpa").get().await()
            currentGpa = try {
                data.value as Double
            } catch (e: ClassCastException) {
                (data.value as Long).toDouble()
            }

            Log.w(TAG, "User's current GPA: $currentGpa")
            currentGpa
        } catch (e: Exception) {
            Log.w(TAG, "Error getting current GPA! ${e.message}")
            currentGpa
        }
    }

    suspend fun getTargetGpa(): Double {
        var targetGpa: Double = 0.0

        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return 0.0
        }

        return try {
            val data = usersRef.child(user!!.uid).child("targetGpa").get().await()
                targetGpa = try {
                    data.value as Double
                } catch (e: ClassCastException) {
                    (data.value as Long).toDouble()
                }

            Log.w(TAG, "User's target GPA: $targetGpa")
            targetGpa
        } catch (e: Exception) {
            Log.w(TAG, "Error getting target GPA! ${e.message}")
            targetGpa
        }
    }

    fun calculateGpa() {
        if (user == null) {
            Log.w(TAG, "User is not authenticated!")
            return
        }

        val rawSum: Double = subjectStorage.get().getSubjects().sumOf { it.overallGrade * it.units }
        Log.w(TAG, "Raw sum GPA of user: $rawSum")
        val gpa: Double = rawSum / subjectStorage.get().getSubjects().sumOf { it.units }
        Log.w(TAG, "Calculated GPA of user: $gpa")
        val finalGpa = gpa / 100 * 5
        Log.w(TAG, "As 5-point GPA: $finalGpa")

        usersRef.child(user!!.uid).child("currentGpa").setValue(finalGpa)
    }

    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
                                .await()
            // put details in user node
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
        onFailure: (Exception) -> Unit) {
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