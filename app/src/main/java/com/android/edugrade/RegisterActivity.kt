package com.android.edugrade

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class RegisterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val login_button = findViewById<Button>(R.id.loginButton)
        login_button.setOnClickListener {
            finish()
        }
    }
}