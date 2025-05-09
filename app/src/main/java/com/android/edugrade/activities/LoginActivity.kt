package com.android.edugrade.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.databinding.ActivityLoginBinding
import com.android.edugrade.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var context: Context
    @Inject
    lateinit var auth: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        context = this
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            showDialog("Please enter your email and password!")
            return
        }

        lifecycleScope.launch {
            auth.loginUser(email, password,
                onSuccess = {
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                },
                onFailure = {
                    showDialog("Error logging in: ${it.message}")
                })
        }
    }
}