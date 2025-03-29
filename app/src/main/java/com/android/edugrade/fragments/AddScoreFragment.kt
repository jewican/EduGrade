package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.edugrade.R
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.databinding.FragmentAddScoreBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddScoreFragment : Fragment(R.layout.fragment_add_score) {
    private lateinit var binding: FragmentAddScoreBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage

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

        val subjectsList = subjectStorage.getSubjects().map { it.code }

        binding.subjectTextView.setAdapter(ArrayAdapter(requireContext(), R.layout.subject_list_item, subjectsList))

        binding.subjectTextView.setOnItemClickListener { _, _, position, _ ->
            val assessmentTypesList = subjectStorage.getAssessmentTypes(subjectsList[position]).map { it.name }
            binding.assessmentTypeTextView.setAdapter(ArrayAdapter(requireContext(), R.layout.subject_list_item, assessmentTypesList))
        }
        
        binding.saveScoreButton.setOnClickListener { saveScore() }
    }
    
    private fun saveScore() {
        Toast.makeText(context, "To be implemented!", Toast.LENGTH_SHORT).show()
    }

}