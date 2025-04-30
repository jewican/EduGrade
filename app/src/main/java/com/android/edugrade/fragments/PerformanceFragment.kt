package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.data.auth.UserRepository
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.databinding.FragmentPerformanceOverviewBinding
import com.android.edugrade.util.BarChartCardAdapter
import javax.inject.Inject

class PerformanceFragment : Fragment(R.layout.fragment_performance_overview) {
    private lateinit var binding: FragmentPerformanceOverviewBinding

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var subjectStorage: SubjectStorage
    @Inject lateinit var scoreStorage: ScoreStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerformanceOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart = subjectStorage.getBarCharts()

        binding.barChart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BarChartCardAdapter(
                subjects = barChart
            )
        }

//        binding.lineChartList.apply {  // later cause fuck off
//            layoutManager = LinearLayoutManager(context)
//            adapter = LineChartCardAdapter (
//                subjects = subjects
//            )
//        }
    }


}