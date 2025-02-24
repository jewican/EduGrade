package com.android.edugrade

import AssessmentType
import Subject
import SubjectStorage
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.databinding.FragmentHomeBinding
import com.android.edugrade.util.SubjectBreakdownAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var subjectStorage: SubjectStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subjectStorage = SubjectStorage(requireContext())

        val subjects = listOf(
            Subject(
                name = "Mathematics",
                assessmentTypes = listOf(
                    AssessmentType("Quizzes", 4.85, 40.0),
                    AssessmentType("Assignments", 4.50, 20.0),
                    AssessmentType("Midterm", 4.75, 40.0)
                )
            ),
            Subject(
                name = "Physics",
                assessmentTypes = listOf(
                    AssessmentType("Laboratory", 4.90, 50.0),
                    AssessmentType("Exams", 4.70, 25.0),
                    AssessmentType("Projects", 4.80, 25.0)
                )
            )
        )

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SubjectBreakdownAdapter(subjects)
        }

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.isNestedScrollingEnabled = false;
    }
}