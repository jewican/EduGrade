package com.android.edugrade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LogOut : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.logout_drawer,
            container, false
        )

        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        confirmButton.setOnClickListener {
            parentFragmentManager.setFragmentResult("logout_request", bundleOf("confirmed" to true))
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
        return view
    }
}

