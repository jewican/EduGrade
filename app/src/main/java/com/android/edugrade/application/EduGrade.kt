package com.android.edugrade.application

import android.app.Application
import com.android.edugrade.util.ChartHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EduGrade : Application() {
    override fun onCreate() {
        super.onCreate()

        ChartHelper.initColors(this)
    }
}