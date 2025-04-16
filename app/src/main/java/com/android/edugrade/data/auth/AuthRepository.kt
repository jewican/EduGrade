package com.android.edugrade.data.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val TAG = "AuthRepository"
    private var auth: FirebaseAuth = Firebase.auth
    private var user: FirebaseUser? = null

    fun currentUser() = user

    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        val usersRef = Firebase.database.reference.child("users")

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

}