package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.data.auth.UserRepository
import com.android.edugrade.databinding.FragmentHomeBinding
import com.android.edugrade.util.SubjectBreakdownAdapter
import com.android.edugrade.data.subject.SubjectStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.apply {
            layoutManager = LinearLayoutManager(context)

            val breakdownAdapter = SubjectBreakdownAdapter()
            subjectStorage.subjects.observe(viewLifecycleOwner) { updatedSubjects ->
                breakdownAdapter.submitList(updatedSubjects)
            }
            adapter = breakdownAdapter
        }

        lifecycleScope.launch {
            binding.homeGpaCard.userCurrentGPA.text =
                String.format("%.2f", userRepository.getCurrentGpa())
            binding.homeGpaCard.userTargetGPA.text =
                String.format("%.2f", userRepository.getTargetGpa())
        }

        binding.homeSubjectBreakdownCard.subjectGradeBreakdownList.isNestedScrollingEnabled = false
    }

}