package com.android.edugrade.util

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.edugrade.R

fun Activity.showError(message: String) {
    AlertDialog.Builder(this).apply {
        setMessage(message)
            .setPositiveButton("Ok") { _, _ -> }
        create()
    }.show()
}

fun FragmentActivity.setCurrentFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        replace(R.id.screenFragment, fragment)
        commit()
    }
}

fun Fragment.setCurrentFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction().apply {
        setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        replace(R.id.screenFragment, fragment)
        addToBackStack("")
        commit()
    }
}