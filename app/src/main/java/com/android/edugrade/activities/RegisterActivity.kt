package com.android.edugrade.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.android.edugrade.R

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