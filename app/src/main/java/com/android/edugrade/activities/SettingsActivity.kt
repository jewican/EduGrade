package com.android.edugrade.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import com.android.edugrade.R

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val swNotif = findViewById<Switch>(R.id.notif)

        swNotif.setOnClickListener{

            //https://www.youtube.com/watch?v=hTdEPnsawiM&list=PLSrm9z4zp4mFttjku-3wiRkPH1lDRQLYy&index=2














        }
    }
}