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
import com.android.edugrade.models.Subject
import com.android.edugrade.util.BarChartCardAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PerformanceFragment : Fragment(R.layout.fragment_performance_overview) {
    private lateinit var binding: FragmentPerformanceOverviewBinding

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var subjectStorage: SubjectStorage
    @Inject lateinit var scoreStorage: ScoreStorage

    val subjectS = ArrayList<BarEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerformanceOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        dataListing()

//        val barData = subjectStorage.getSubjects()
//
        for (i in 0..5){
//            subjectS.add(BarEntry(i.toFloat(), barData[i].overallGrade.toFloat()))
            subjectS.add(BarEntry(i.toFloat(), i.toFloat()))
        }

        binding.barChart.description.isEnabled = false
        binding.barChart.setMaxVisibleValueCount(5)
        binding.barChart.setPinchZoom(false)
        binding.barChart.setDrawBarShadow(false)
        binding.barChart.setDrawGridBackground(false)

        val xAxis = binding.barChart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayListOf("Subjects", "GPA"))

        binding.barChart.axisLeft.setDrawGridLines(false)
        binding.barChart.legend.isEnabled = false

        val barDataSetter: BarDataSet

        if (binding.barChart.data != null && binding.barChart.data.dataSetCount > 0){
            barDataSetter = binding.barChart.data.getDataSetByIndex(0) as BarDataSet
            barDataSetter.values = subjectS
            binding.barChart.data.notifyDataChanged()
            binding.barChart.notifyDataSetChanged()
        } else {
            barDataSetter = BarDataSet(subjectS, "Subject")
            barDataSetter.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            barDataSetter.setDrawValues(false)

            val dataSet = ArrayList<IBarDataSet>()
            dataSet.add(barDataSetter)

            val data = BarData(dataSet)
            binding.barChart.data = data
            binding.barChart.setFitBars(true)
        }

        binding.barChart.invalidate()
    }

//    private fun dataListing(){
//

//
//       setChart()
//    }

//    private fun setChart(){
//        binding.barChart.description.isEnabled = false
//        binding.barChart.setMaxVisibleValueCount(5)
//        binding.barChart.setPinchZoom(false)
//        binding.barChart.setDrawBarShadow(false)
//        binding.barChart.setDrawGridBackground(false)
//
//        val xAxis = binding.barChart.xAxis
//
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)
//        xAxis.granularity = 1f
//        xAxis.isGranularityEnabled = true
//        xAxis.valueFormatter = IndexAxisValueFormatter(arrayListOf("Subjects", "GPA"))
//
//        binding.barChart.axisLeft.setDrawGridLines(false)
//        binding.barChart.legend.isEnabled = false
//
//        val barDataSetter: BarDataSet
//
//        if (binding.barChart.data != null && binding.barChart.data.dataSetCount > 0){
//            barDataSetter = binding.barChart.data.getDataSetByIndex(0) as BarDataSet
//            barDataSetter.values = subjectS
//            binding.barChart.data.notifyDataChanged()
//            binding.barChart.notifyDataSetChanged()
//        } else {
//            barDataSetter = BarDataSet(subjectS, "Subject")
//            barDataSetter.setColors(*ColorTemplate.VORDIPLOM_COLORS)
//            barDataSetter.setDrawValues(false)
//
//            val dataSet = ArrayList<IBarDataSet>()
//            dataSet.add(barDataSetter)
//
//            val data = BarData(dataSet)
//            binding.barChart.data = data
//            binding.barChart.setFitBars(true)
//        }
//
//        binding.barChart.invalidate()
//    }

}