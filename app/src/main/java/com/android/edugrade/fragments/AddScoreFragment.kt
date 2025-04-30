package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.edugrade.R
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.databinding.FragmentAddScoreBinding
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Score
import com.android.edugrade.models.Subject
import com.android.edugrade.util.showError
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AddScoreFragment : Fragment(R.layout.fragment_add_score) {
    private lateinit var binding: FragmentAddScoreBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var scoreStorage: ScoreStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddScoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subjectStorage.subjects.observe(viewLifecycleOwner) { subjectList ->
            val subjectsList = subjectList.map { it.code }
            var selectedSubject = Subject()
            var semesterHalves: List<String>

            val subjectAdapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, subjectsList)
            binding.subjectTextView.setAdapter(subjectAdapter)

            binding.subjectTextView.setOnItemClickListener { _, _, position, _ ->
                selectedSubject = subjectList[position]
                semesterHalves = selectedSubject.assessmentTypes.map { it.name }
                val semesterAdapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, semesterHalves)
                binding.semesterHalfTextView.setAdapter(semesterAdapter)
            }

            binding.semesterHalfTextView.setOnItemClickListener { _, _, position, _ ->
                val selectedHalf = selectedSubject.assessmentTypes[position]
                val assessmentTypesList = selectedHalf.getLeafNodes().map { it.name }
                val assessmentAdapter = ArrayAdapter(requireContext(), R.layout.subject_list_item, assessmentTypesList)
                binding.assessmentTypeTextView.setAdapter(assessmentAdapter)
            }
        }

        binding.saveScoreButton.setOnClickListener {
            saveScore()
        }
    }

    private fun saveScore() {
        val code = binding.subjectTextView.text.toString()
        val assessmentType = binding.assessmentTypeTextView.text.toString()
        val name = binding.etActName.text.toString()
        val userScore = binding.etScore.text.toString().toDouble()
        val totalScore = binding.etTscore.text.toString().toDouble()
        val dateAdded = LocalDateTime.now()

        scoreStorage.addScore(
            Score(
                code = code,
                assessmentType = assessmentType,
                name = name,
                userScore = userScore,
                totalScore = totalScore,
                dateAdded = dateAdded,
            ),
            onSuccess = {
                subjectStorage.recalculateSubject(code)
                parentFragmentManager.popBackStack()
            },
            onFailure = { exception ->
                showError("Error saving score! $exception")
            }
        )
    }

}