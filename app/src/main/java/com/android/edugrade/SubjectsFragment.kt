package com.android.edugrade

import com.android.edugrade.util.SubjectStorage
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.edugrade.databinding.FragmentSubjectsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectsFragment : Fragment() {
    @Inject
    lateinit var subjectStorage: SubjectStorage
    private lateinit var binding: FragmentSubjectsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subjects = subjectStorage.loadSubjects()

        subjects.forEach {
            val subjectView = layoutInflater
                .inflate(R.layout.subjects_subject_card, binding.subjectsList, false)

            val subjectCode = subjectView.findViewById<TextView>(R.id.subjectName)
            val subjectDescription = subjectView.findViewById<TextView>(R.id.subjectDescription)

            subjectCode.text = it.code
            subjectDescription.text = it.description

            binding.subjectsList.addView(subjectView)
        }
    }
}