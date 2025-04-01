package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.databinding.FragmentScoresBinding
import com.android.edugrade.util.ScoreCardAdapter
import com.android.edugrade.util.setCurrentFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScoresFragment : Fragment(R.layout.fragment_scores) {
    private lateinit var binding: FragmentScoresBinding
    @Inject
    lateinit var scoreStorage: ScoreStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoresBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val scores = scoreStorage.getAllScores()

        binding.addScoreButton.setOnClickListener {
            setCurrentFragment(AddScoreFragment())
        }

        binding.scoresList.apply { 
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(
                scoresList = scores,
                onClick = {
                    Toast.makeText(context, "To be implemented!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

}