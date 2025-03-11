package com.android.edugrade.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.models.Subject
import com.google.android.material.progressindicator.LinearProgressIndicator

class SubjectBreakdownAdapter(private val subjects: List<Subject>) :
    RecyclerView.Adapter<SubjectBreakdownAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.subjectName)
        val assessmentTypesList: LinearLayout = itemView.findViewById(R.id.assessmentTypesList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_breakdown, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.subjectName.text = subject.name

        holder.assessmentTypesList.removeAllViews()

        subject.assessmentTypes.forEach { assessmentType ->
            val assessmentView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.assessment_type, holder.assessmentTypesList, false)

            // Bind assessment type data
            val typeText = assessmentView.findViewById<TextView>(R.id.assessmentType)
            val gradeText = assessmentView.findViewById<TextView>(R.id.gradeValue)
            val progressIndicator = assessmentView.findViewById<LinearProgressIndicator>(R.id.gradeVisual)

            typeText.text = assessmentType.name
            gradeText.text = String.format("%.2f", assessmentType.grade)
            progressIndicator.progress = (assessmentType.grade * 100).toInt()

            holder.assessmentTypesList.addView(assessmentView)
        }
    }

    override fun getItemCount() = subjects.size
}