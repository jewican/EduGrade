package com.android.edugrade.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.data.auth.UserRepository
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.databinding.SubjectGpaBarChartBinding
import com.android.edugrade.models.BarChartData
import com.android.edugrade.models.Subject
import kotlin.collections.mapIndexed
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import javax.inject.Inject

class BarChartCardAdapter(
    private val subjects: List<BarChartData>
) : RecyclerView.Adapter<BarChartCardAdapter.BarChartViewHolder>() {
    @Inject lateinit var subjectStorage: SubjectStorage

    class BarChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SubjectGpaBarChartBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BarChartViewHolder {
        val chartView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_gpa_bar_chart, parent, false)

        return BarChartViewHolder(chartView)
    }

    override fun getItemCount() = subjects.size

    override fun onBindViewHolder(
        holder: BarChartViewHolder,
        position: Int
    ) {
        val data = subjects[position]
        var subjectList = mutableListOf<String>()

        holder.binding.tvSubjectDesc.text = "Every Subject and Grade"
        val subs = data.grades.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(subs, "Subjects n Grade")
        dataSet.color = Color.BLUE

        val barData = BarData(dataSet)
        holder.binding.barChart.data = barData

        val xAxis = holder.binding.barChart.xAxis
        data.subjectS.forEach { subject ->
            subjectList.add(subject.description)
        }
        xAxis.valueFormatter = IndexAxisValueFormatter(subjectList)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawLabels(true)

        holder.binding.barChart.axisRight.isEnabled = false
        holder.binding.barChart.description.isEnabled = false
        holder.binding.barChart.invalidate()
    }


}