package com.android.edugrade.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.databinding.FragmentSubjectsBinding
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.util.SubjectCardAdapter
import com.android.edugrade.util.setCurrentFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectsFragment : Fragment() {
    private lateinit var binding: FragmentSubjectsBinding
    @Inject lateinit var subjectStorage: SubjectStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.subjectsList
        recyclerView.layoutManager = LinearLayoutManager(context)

        subjectStorage.subjects.observe(viewLifecycleOwner) { subjects ->
            recyclerView.adapter = SubjectCardAdapter(
                subjects = subjects,
                onClick = { code ->
                    setCurrentFragment(AddSubjectFragment(code))
                }
            )
        }

        binding.addSubjectButton.setOnClickListener {
            setCurrentFragment(AddSubjectFragment())
        }
    }

}