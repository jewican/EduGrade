package com.android.edugrade.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.android.edugrade.databinding.AddSubjectActivityTypesItemBinding
import com.android.edugrade.databinding.AddSubjectTimeslotItemBinding
import com.android.edugrade.databinding.FragmentAddSubjectBinding
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Subject
import com.android.edugrade.models.Timeslot
import com.android.edugrade.util.SubjectStorage
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AddSubjectFragment(
    val code: String? = null  // TODO: populate details when given a subject code
) : Fragment() {
    private lateinit var binding: FragmentAddSubjectBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (code != null) {
            binding.addSubjectButton.visibility = View.GONE
        }

        binding.timeslotsCard.initialTimeslot.timeslotTimeIn.setOnClickListener {
            showTimePickerDialog(binding.timeslotsCard.initialTimeslot.timeslotTimeIn)
        }
        binding.timeslotsCard.initialTimeslot.timeslotTimeOut.setOnClickListener {
            showTimePickerDialog(binding.timeslotsCard.initialTimeslot.timeslotTimeOut)
        }

        binding.addSubjectButton.setOnClickListener {
            saveNewSubject()
        }

        binding.timeslotsCard.addTimeslotButton.setOnClickListener {
            addTimeslot()
        }

        binding.activityTypesCard.addTypeButton.setOnClickListener {
            addActivityType()
        }
    }

    private fun addTimeslot() {
        val linearLayout = binding.timeslotsCard.subjectTimeslots
        val binding = AddSubjectTimeslotItemBinding.inflate(
            layoutInflater,
            linearLayout,
            false
        )

        binding.timeslotTimeIn.setOnClickListener {
            showTimePickerDialog(binding.timeslotTimeIn)
        }

        binding.timeslotTimeOut.setOnClickListener {
            showTimePickerDialog(binding.timeslotTimeOut)
        }

        val deleteTimeslotButton = binding.deleteTimeslotButton
        deleteTimeslotButton.visibility = View.VISIBLE

        deleteTimeslotButton.setOnClickListener {
            linearLayout.removeView(deleteTimeslotButton.parent as ViewGroup)
        }

        linearLayout.addView(binding.root)
    }

    private fun addActivityType() {
        val linearLayout = binding.activityTypesCard.subjectActivityTypes
        val binding = AddSubjectActivityTypesItemBinding.inflate(
            layoutInflater,
            linearLayout,
            false
        )

        val deleteWeightButton = binding.deleteWeightButton
        deleteWeightButton.visibility = View.VISIBLE

        deleteWeightButton.setOnClickListener {
            linearLayout.removeView(deleteWeightButton.parent as ViewGroup)
        }

        linearLayout.addView(binding.root)
    }

    private fun saveNewSubject() {
        val code = binding.generalCard.subjectCode.text.toString()
        val description = binding.generalCard.subjectDescription.text.toString()
        val instructor = binding.generalCard.subjectInstructor.text.toString()

        val timeslots = getTimeslots()
        val assessmentTypes = getAssessmentTypes()

        Log.wtf("AddSubjectFragment: Timeslots", timeslots.toString())
        Log.wtf("AddSubjectFragment: AssessmentTypes", assessmentTypes.toString())

        subjectStorage.addSubject(
            Subject(
                code = code,
                description = description,
                instructor = instructor,
                timeslots = timeslots,
                assessmentTypes = assessmentTypes,
                overallGrade = 5.0
            )
        )
    }

    private fun getTimeslots(): MutableList<Timeslot> {
        val timeslots: MutableList<Timeslot> = mutableListOf()
        val linearLayout = binding.timeslotsCard.subjectTimeslots
        var child: AddSubjectTimeslotItemBinding
        for (i in 0 until  linearLayout.childCount) {
            child = AddSubjectTimeslotItemBinding.bind(linearLayout.getChildAt(i))
            val (startHour, startMinute) = child.timeslotTimeIn.text.split(":").map { it.toInt() }
            val (endHour, endMinute) = child.timeslotTimeOut.text.split(":").map { it.toInt() }
            timeslots.add(
                Timeslot(
                    startTime = LocalTime.of(startHour, startMinute),
                    endTime = LocalTime.of(endHour, endMinute)
                )
            )
        }
        return timeslots
    }

    private fun getAssessmentTypes(): MutableList<AssessmentType> {
        val assessmentTypes: MutableList<AssessmentType> = mutableListOf()
        val linearLayout = binding.activityTypesCard.subjectActivityTypes
        var child: AddSubjectActivityTypesItemBinding
        for (i in 0 until  linearLayout.childCount) {
            child = AddSubjectActivityTypesItemBinding.bind(linearLayout.getChildAt(i))
            assessmentTypes.add(
                AssessmentType(
                    name = child.activityType.text.toString(),
                    weight = child.activityWeight.text.toString().toDouble()
                )
            )
        }
        return assessmentTypes
    }

    private fun showTimePickerDialog(timeInput: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Display the TimePickerDialog
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            // Format the time with leading zeros (HH:mm)
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeInput.setText(time)
        }, hour, minute, true).show()
    }
}