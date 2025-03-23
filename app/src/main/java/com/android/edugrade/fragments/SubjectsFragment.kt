package com.android.edugrade.fragments

import com.android.edugrade.util.SubjectStorage
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.edugrade.R
import com.android.edugrade.databinding.FragmentSubjectsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectsFragment : Fragment() {
    private lateinit var binding: FragmentSubjectsBinding
    @Inject lateinit var subjectStorage: SubjectStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get subjects list
        val subjects = subjectStorage.getSubjects()

        // Create views per subject
        subjects.forEach {
            val subjectView = layoutInflater
                .inflate(R.layout.subjects_subject_card, binding.subjectsList, false)

            val subjectCode = subjectView.findViewById<TextView>(R.id.subjectName)
            val subjectDescription = subjectView.findViewById<TextView>(R.id.subjectDescription)

            subjectCode.text = it.code
            subjectDescription.text = it.description

            subjectView.setOnClickListener {
                Toast.makeText(context, "To be implemented!", Toast.LENGTH_SHORT).show()
            }

            binding.subjectsList.addView(subjectView)
        }
    }
}