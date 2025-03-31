package com.android.edugrade.util

import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.databinding.AddSubjectActivityTypesItemBinding
import com.android.edugrade.models.AssessmentType

class GradingSystemAdapter(
    private val assessmentTypeNodes: MutableList<AssessmentType>,
    private val onAddChild: (AssessmentType) -> Unit,
) : RecyclerView.Adapter<GradingSystemAdapter.AssessmentTypeHolder>() {

    private val TAG = "GradingSystemAdapter"
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
    }

    inner class AssessmentTypeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = AddSubjectActivityTypesItemBinding.bind(view)

        fun bind(assessmentTypeNode: AssessmentType) {
            val childAdapter = GradingSystemAdapter(
                assessmentTypeNode.assessmentTypes,
                onAddChild,
            )

            binding.activityType.apply {
                removeTextChangedListener(this.tag as? TextWatcher)
                val typeTextWatcher = doAfterTextChanged { text ->
                    assessmentTypeNode.name = text.toString()
                }
                tag = typeTextWatcher
            }

            binding.activityWeight.apply {
                removeTextChangedListener(this.tag as? TextWatcher)
                val weightTextWatcher = doAfterTextChanged { text ->
                    assessmentTypeNode.weight = text.toString().toDouble()
                }
                tag = weightTextWatcher
            }

            binding.assessmentTypesList.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = childAdapter
                visibility = if (assessmentTypeNode.assessmentTypes.isNotEmpty()) View.VISIBLE else View.GONE
            }

            binding.deleteWeightButton.setOnClickListener {
                removeChild(assessmentTypeNode)
            }
            binding.addCategoryButton.setOnClickListener { onAddChild(assessmentTypeNode) }

            binding.assessmentTypesList.post {
                binding.assessmentTypesList.invalidate()
                binding.assessmentTypesList.requestLayout()
            }

            binding.activityType.setText(assessmentTypeNode.name)
            binding.activityWeight.setText(assessmentTypeNode.weight.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentTypeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_subject_activity_types_item, parent, false)

        return AssessmentTypeHolder(view)
    }

    override fun getItemCount(): Int = assessmentTypeNodes.size

    override fun onBindViewHolder(holder: AssessmentTypeHolder, position: Int) {
        holder.bind(assessmentTypeNodes[position])
    }

    fun addChild(parent: AssessmentType, child: AssessmentType) {
        val res = parent.assessmentTypes.add(child)
        Log.wtf(TAG, "Added: $res | Current children: ${parent.assessmentTypes.size}")
        notifyDataSetChanged()

        val index = assessmentTypeNodes.indexOf(parent)
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    fun removeChild(assessmentType: AssessmentType) {
        val res = assessmentTypeNodes.remove(assessmentType)
        Log.wtf(TAG, "Removed: $res | Current children: ${assessmentTypeNodes.size}")
        notifyDataSetChanged()
    }

    fun addRootNode(node: AssessmentType) {
        val res = assessmentTypeNodes.add(node)
        for (node in assessmentTypeNodes) {
            Log.wtf(TAG, "${node.name}: ${System.identityHashCode(node)} ")
        }
        Log.wtf(TAG, "Added Root: $res | Current children: ${assessmentTypeNodes.size}")
        notifyDataSetChanged()
    }
}