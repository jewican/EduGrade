package com.android.edugrade.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.android.edugrade.R

class ProfileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnConfirm = findViewById<Button>(R.id.btn_confirm)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val etFName = findViewById<EditText>(R.id.et_first_name)
        val etLName = findViewById<EditText>(R.id.et_last_name)



        btnConfirm.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}