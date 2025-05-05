package com.android.edugrade.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.android.edugrade.R
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.fragments.HomeFragment
import com.android.edugrade.fragments.LogOut
import com.android.edugrade.fragments.PerformanceFragment
import com.android.edugrade.fragments.ProfileFragment
import com.android.edugrade.fragments.ScoresFragment
import com.android.edugrade.fragments.SubjectsFragment
import com.android.edugrade.util.setCurrentFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var scoreStorage: ScoreStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subjectStorage.loadSubjects()
        scoreStorage.loadScores()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val settingsNotif = findViewById<Button>(R.id.btnNotif)
        val homeFragment = HomeFragment()
        val scoresFragment = ScoresFragment()
        val subjectsFragment = SubjectsFragment()
        val performanceFragment = PerformanceFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        settingsNotif.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        bottomNavigationView.setOnItemSelectedListener {
            while (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.scores -> setCurrentFragment(scoresFragment)
                R.id.subjects -> setCurrentFragment(subjectsFragment)
                R.id.performance -> setCurrentFragment(performanceFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
            }
            true
        }

        supportFragmentManager.setFragmentResultListener("logout_request", this) { _, bundle ->
            val confirmed = bundle.getBoolean("confirmed")
            if (confirmed) {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount != 0) {
                    supportFragmentManager.popBackStack()
                    return
                }
                val bottomSheet = LogOut()
                bottomSheet.show(
                    supportFragmentManager,
                    "ModalBottomSheet"
                )
            }
        })
    }
}