package com.android.edugrade

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button_login = findViewById<Button>(R.id.loginButton)
        button_login.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}