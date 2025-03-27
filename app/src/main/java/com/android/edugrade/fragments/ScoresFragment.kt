package com.android.edugrade.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.edugrade.R
import com.android.edugrade.databinding.FragmentScoresBinding
import com.android.edugrade.util.setCurrentFragment
import dagger.hilt.android.scopes.FragmentScoped

class ScoresFragment : Fragment(R.layout.fragment_scores) {
    private lateinit var binding: FragmentScoresBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoresBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addScoreButton.setOnClickListener {
            setCurrentFragment(AddScoreFragment())
        }
    }

}