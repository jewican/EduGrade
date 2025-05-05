package com.android.edugrade.util

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.databinding.SubjectGpaLineChartCardBinding
import com.android.edugrade.models.Subject


class LineChartCardAdapter (
    private val subjects: List<Subject>
) : RecyclerView.Adapter<LineChartCardAdapter.LineChartViewHolder>() {

    class LineChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SubjectGpaLineChartCardBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LineChartViewHolder {
        val lineChartView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_gpa_line_chart_card, parent, false)

        return LineChartViewHolder(lineChartView)
    }

    override fun onBindViewHolder(
        holder: LineChartViewHolder,
        position: Int
    ) {
        holder.binding.tvSubjectDesc.text = subjects[position].description
        holder.binding.lineChart
    }

    override fun getItemCount(): Int = subjects.size

    fun dataList(){

    }
}