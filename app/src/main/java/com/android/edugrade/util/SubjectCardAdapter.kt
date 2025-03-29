package com.android.edugrade.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.databinding.SubjectsSubjectCardBinding
import com.android.edugrade.models.Subject

class SubjectCardAdapter(
    private val subjects: List<Subject>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SubjectCardAdapter.SubjectViewHolder>() {

        class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding = SubjectsSubjectCardBinding.bind(itemView)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubjectViewHolder {
        val subjectView = LayoutInflater.from(parent.context)
            .inflate(R.layout.subjects_subject_card, parent, false)

        return SubjectViewHolder(subjectView)
    }

    override fun getItemCount() = subjects.size

    override fun onBindViewHolder(
        holder: SubjectViewHolder,
        position: Int
    ) {
        holder.binding.subjectName.text = subjects[position].code
        holder.binding.subjectDescription.text = subjects[position].description
        holder.binding.root.setOnClickListener {
            onClick(subjects[position].code)
        }
    }
}