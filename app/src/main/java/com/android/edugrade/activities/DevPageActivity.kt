package com.android.edugrade.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.android.edugrade.R

class DevPageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_page)

        val button = findViewById<Button>(R.id.homePage)
    }
}