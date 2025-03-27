package com.android.edugrade.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.edugrade.R

fun FragmentActivity.setCurrentFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        replace(R.id.screenFragment, fragment)
        commit()
    }
}

fun Fragment.setCurrentFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction().apply {
        replace(R.id.screenFragment, fragment)
        addToBackStack("")
        commit()
    }
}