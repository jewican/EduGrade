package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.databinding.FragmentHomeBinding
import com.android.edugrade.util.SubjectBreakdownAdapter
import com.android.edugrade.util.SubjectStorage
import javax.inject.Inject

class ViewScoresFragment : Fragment(R.layout.fragment_view_scores) {
    private lateinit var binding: FragmentHomeBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage

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

        subjectStorage.loadSubjects()

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SubjectBreakdownAdapter(subjectStorage.getSubjects())
        }

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.isNestedScrollingEnabled = false;
    }
}