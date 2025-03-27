package com.android.edugrade.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.edugrade.R
import com.android.edugrade.fragments.HomeFragment
import com.android.edugrade.fragments.LogOut
import com.android.edugrade.fragments.PerformanceFragment
import com.android.edugrade.fragments.ProfileFragment
import com.android.edugrade.fragments.ScoresFragment
import com.android.edugrade.fragments.SubjectsFragment
import com.android.edugrade.util.setCurrentFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val homeFragment = /*supportFragmentManager.findFragmentByTag("HomeFragment") ?: */
            HomeFragment()
        val scoresFragment = /*supportFragmentManager.findFragmentByTag("ScoresFragment") ?: */
            ScoresFragment()
        val subjectsFragment = /*supportFragmentManager.findFragmentByTag("SubjectsFragment") ?: */
            SubjectsFragment()
        val performanceFragment = /*supportFragmentManager.findFragmentByTag("PerformanceFragment") ?: */
            PerformanceFragment()
        val profileFragment = /*supportFragmentManager.findFragmentByTag("ProfileFragment") ?: */
            ProfileFragment()

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
                if (supportFragmentManager.backStackEntryCount != 0) {
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