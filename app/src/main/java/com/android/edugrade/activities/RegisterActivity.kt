package com.android.edugrade.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.databinding.ActivityRegisterBinding
import com.android.edugrade.util.isValidEmail
import com.android.edugrade.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    @Inject
    lateinit var auth: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { finish() }

        binding.registerButton.setOnClickListener { startRegister() }
    }

    private fun startRegister() {
        val username = binding.usernameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password1 = binding.password1EditText.text.toString()
        val password2  = binding.password2EditText.text.toString()

        if (username.isBlank()  ||
            email.isBlank()     ||
            password1.isBlank() ||
            password2.isBlank()) {
            showDialog("Please fill out all fields!")
            return
        }

        if (!email.isValidEmail()) {
            showDialog("Email is not valid!")
            return
        }

        if (password1 != password2) {
            showDialog("Passwords do not match!")
            return
        }

        lifecycleScope.launch {
            if (auth.registerUser(username, email, password1)) {
                showDialog("Registration successful!") {
                    finish()
                }
            } else {
                showDialog("Registration failed! Network may be unstable.")
            }
        }

    }
}