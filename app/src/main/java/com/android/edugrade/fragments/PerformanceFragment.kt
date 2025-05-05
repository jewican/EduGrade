package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.databinding.FragmentPerformanceBinding
import com.android.edugrade.util.ChartHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PerformanceFragment : Fragment() {
    private lateinit var binding: FragmentPerformanceBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerformanceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gpaLineChart = binding.gpaLineChart
        userRepository.getGpaHistory { gpaHistory ->
            ChartHelper.setupGpaLineChart(gpaLineChart, gpaHistory)
        }

        val subjectBarChart = binding.subjectBarChart
        ChartHelper.setupSubjectGradesBarChart(subjectBarChart, subjectStorage.subjects.value!!)

    }
}