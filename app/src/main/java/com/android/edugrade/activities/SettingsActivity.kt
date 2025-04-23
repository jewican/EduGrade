package com.android.edugrade.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Switch
import com.android.edugrade.R

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val swNotif = findViewById<Switch>(R.id.notif)

        // https://www.youtube.com/watch?v=_Z2S63O-1HE

    }
}