package com.android.edugrade.data.score

import com.android.edugrade.models.Subject
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.GsonBuilder

class ScoreStorage {
    private lateinit var scores: MutableList<Subject>
    private val gson = Converters.registerLocalTime(GsonBuilder()).create()
    private val filename = "subjects.json"
}