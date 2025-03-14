package com.android.edugrade

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val homeFragment = HomeFragment()
        val scoresFragment = ScoresFragment()
        val subjectsFragment = SubjectsFragment()
        val performanceFragment = PerformanceFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener {
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
                val bottomSheet =
                    LogOut()
                bottomSheet.show(
                    supportFragmentManager,
                    "ModalBottomSheet"
                )
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.screenFragment, fragment)
            commit()
        }
    }
}