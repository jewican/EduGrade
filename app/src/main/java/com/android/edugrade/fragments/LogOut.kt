package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import com.android.edugrade.R
import com.android.edugrade.databinding.LogoutDrawerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth

class LogOut : BottomSheetDialogFragment() {
    private lateinit var binding: LogoutDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.LogOutBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogoutDrawerBinding.inflate(layoutInflater)

        binding.confirmButton.setOnClickListener {
            parentFragmentManager.setFragmentResult("logout_request", bundleOf("confirmed" to true))
            Firebase.auth.signOut()
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}

