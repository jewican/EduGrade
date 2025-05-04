package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.edugrade.R
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.databinding.FragmentProfileTestBinding
import com.android.edugrade.util.saveToFile
import com.android.edugrade.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile_test) {
    private lateinit var binding: FragmentProfileTestBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var scoreStorage: ScoreStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exportSubjectsButton.setOnClickListener {
            showDialog("Not yet implemented!")
        }

        binding.exportScoresButton.setOnClickListener {
            scoreStorage.exportScoresToJson(
                onSuccess = { jsonString ->
                    val uri = jsonString.saveToFile(requireContext(), "scores_export_${LocalDateTime.now()}.json")
                    if (uri != null) {
                        showDialog("Scores exported successfully!")
                    }
                },
                onFailure = { exception ->
                    showDialog("Export failed: ${exception.message}")
                }
            )
        }
    }
}