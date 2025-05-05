package com.android.edugrade.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.edugrade.R
import com.android.edugrade.activities.DevPageActivity
import com.android.edugrade.activities.ProfileActivity
import com.android.edugrade.databinding.FragmentPerformanceOverviewBinding
import com.android.edugrade.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAboutUs.setOnClickListener{
            startActivity(Intent(activity, DevPageActivity::class.java))
        }

        binding.btnEditPfp.setOnClickListener{
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
    }
}