package com.android.edugrade.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.models.Subject
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Locale

class SubjectBreakdownAdapter :
    ListAdapter<Subject, SubjectBreakdownAdapter.SubjectViewHolder>(SubjectDiffCallback()) {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.subjectName)
        val assessmentTypesList: LinearLayout = itemView.findViewById(R.id.assessmentTypesList)
    }

    class SubjectDiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_breakdown, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = getItem(position)
        holder.subjectName.text = subject.code

        holder.assessmentTypesList.removeAllViews()

        subject.assessmentTypes.forEach { assessmentType ->
            val assessmentView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.assessment_type, holder.assessmentTypesList, false)

            val typeText = assessmentView.findViewById<TextView>(R.id.assessmentType)
            val gradeText = assessmentView.findViewById<TextView>(R.id.gradeValue)
            val progressIndicator = assessmentView.findViewById<LinearProgressIndicator>(R.id.gradeVisual)

            typeText.text = assessmentType.name
            gradeText.text = String.format(Locale("en", "PH"), "%.1f", assessmentType.grade)
            progressIndicator.progress = (assessmentType.grade * 100).toInt()

            holder.assessmentTypesList.addView(assessmentView)
        }
    }
}