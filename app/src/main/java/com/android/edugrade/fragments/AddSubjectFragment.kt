package com.android.edugrade.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.databinding.AddSubjectActivityTypesItemBinding
import com.android.edugrade.databinding.AddSubjectTimeslotItemBinding
import com.android.edugrade.databinding.FragmentAddSubjectBinding
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Subject
import com.android.edugrade.models.Timeslot
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.util.GradingSystemAdapter
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddSubjectFragment(
    val code: String? = null
) : Fragment() {
    private lateinit var binding: FragmentAddSubjectBinding
    private lateinit var semesterPartAdapter: GradingSystemAdapter
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

        binding.timeslotsCard.addTimeslotButton.setOnClickListener {
            addTimeslot()
        }

        binding.activityTypesCard.addSemesterPartButton.setOnClickListener {
            addAssessmentType(null)
        }

        binding.addSubjectButton.setOnClickListener {
            if (saveSubject()) parentFragmentManager.popBackStack()
        }

        semesterPartAdapter = GradingSystemAdapter(
            mutableListOf(),
            onAddChild = { parent -> addAssessmentType(parent) }
        )

        binding.activityTypesCard.subjectSemesterParts.apply {
            adapter = semesterPartAdapter
            layoutManager = LinearLayoutManager(context)
        }

        if (code != null) {
            editSubjectMode()
            return
        }

        addTimeslot()
        addAssessmentType(null)
    }

    private fun addAssessmentType(parent: AssessmentType?) {
        val newAssessmentNode = AssessmentType()

        if (parent == null) {
            semesterPartAdapter.addRootNode(newAssessmentNode)
        } else {
            semesterPartAdapter.addChild(parent, newAssessmentNode)
        }

        binding.activityTypesCard.subjectSemesterParts.post {
            binding.activityTypesCard.subjectSemesterParts.invalidate()
            binding.activityTypesCard.subjectSemesterParts.requestLayout()
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

    private fun saveSubject(): Boolean {
        val code = binding.generalCard.subjectCode.text.toString()
        val description = binding.generalCard.subjectDescription.text.toString()
        val instructor = binding.generalCard.subjectInstructor.text.toString()

        var timeslots: List<Timeslot>
        var assessmentTypes: List<AssessmentType>
        try {
            timeslots = getTimeslots()
            assessmentTypes = getAssessmentTypes()
        } catch (e: Exception) {
            showAlert(e.message!!)
            return false
        }

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
        return true
    }

    private fun getTimeslots(): MutableList<Timeslot> {
        val timeslots: MutableList<Timeslot> = mutableListOf()
        val linearLayout = binding.timeslotsCard.subjectTimeslots
        var child: AddSubjectTimeslotItemBinding
        for (i in 0 until  linearLayout.childCount) {
            child = AddSubjectTimeslotItemBinding.bind(linearLayout.getChildAt(i))

            try {
                val (startHour, startMinute) = child.timeslotTimeIn.text.split(":")
                    .map { it.toInt() }
                val (endHour, endMinute) = child.timeslotTimeOut.text.split(":").map { it.toInt() }

                val selectedDay = child.dayOfWeekChipGroup.checkedChipId
                if (selectedDay == View.NO_ID) {
                    throw IllegalArgumentException("Please select a day of the week for all timeslots!")
                }
                val dayOfWeek = child.root.findViewById<Chip>(selectedDay).tag as String

                timeslots.add(
                    Timeslot(
                        type = child.timeslotType.text.toString(),
                        dayOfWeek = DayOfWeek.valueOf(dayOfWeek),
                        startTime = LocalTime.of(startHour, startMinute),
                        endTime = LocalTime.of(endHour, endMinute)
                    )
                )
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Please fill out all timeslot fields!")
            }
        }
        return timeslots
    }

    private fun getAssessmentTypes(): MutableList<AssessmentType> {
        val assessmentTypes: MutableList<AssessmentType> = mutableListOf()
        val linearLayout = binding.activityTypesCard.subjectSemesterParts
        var child: AddSubjectActivityTypesItemBinding
        try {
            for (i in 0 until linearLayout.childCount) {
                child = AddSubjectActivityTypesItemBinding.bind(linearLayout.getChildAt(i))

                assessmentTypes.add(
                    AssessmentType(
                        name = child.activityType.text.toString(),
                        weight = child.activityWeight.text.toString().toDouble()
                    )
                )
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Please enter weights as Double (0.0 - 1.0)!")
        }
        return assessmentTypes
    }

    private fun showTimePickerDialog(timeInput: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val time = String.format(Locale("en", "PH"), "%02d:%02d", selectedHour, selectedMinute)
            timeInput.setText(time)
        }, hour, minute, true).show()
    }

    private fun editSubjectMode() {
        val subject = subjectStorage.getSubject(code!!)

        binding.generalCard.subjectCode.setText(subject.code)
        binding.generalCard.subjectCode.focusable = View.NOT_FOCUSABLE

        binding.generalCard.subjectDescription.setText(subject.description)
        binding.generalCard.subjectInstructor.setText(subject.instructor)

        var timeslotView: View
        var timeslotBinding: AddSubjectTimeslotItemBinding
        for (i in 0 until subject.timeslots.size) {
            addTimeslot()

            timeslotView = binding.timeslotsCard.subjectTimeslots.getChildAt(i)
            timeslotBinding = AddSubjectTimeslotItemBinding.bind(timeslotView)

            timeslotBinding.timeslotType
                .setText(subject.timeslots[i].type)
            timeslotBinding.timeslotTimeIn
                .setText(subject.timeslots[i].startTime.toString())
            timeslotBinding.timeslotTimeOut
                .setText(subject.timeslots[i].endTime.toString())

            (timeslotBinding.dayOfWeekChipGroup.getChildAt(subject.timeslots[i].dayOfWeek.value - 1) as Chip)
                .let { chip ->
                    chip.isChecked = true
                    timeslotBinding.dayOfWeekScrollView.let {
                        it.post {
                            val chipCenterX = chip.left + chip.width / 2
                            val scrollCenterX = it.width / 2
                            val scrollX = chipCenterX - scrollCenterX
                            it.smoothScrollTo(scrollX, 0)
                        }
                    }
                }

        }

        var activityTypeView: View
        var activityTypeBinding: AddSubjectActivityTypesItemBinding
        for (i in 0 until subject.assessmentTypes.size) {
            addAssessmentType(null)

            activityTypeView = binding.activityTypesCard.subjectSemesterParts.getChildAt(i)
            activityTypeBinding = AddSubjectActivityTypesItemBinding.bind(activityTypeView)

            activityTypeBinding.activityType.setText(subject.assessmentTypes[i].name)
            activityTypeBinding.activityWeight.setText(subject.assessmentTypes[i].weight.toString())
        }

        binding.addSubjectButton.text = "SAVE CHANGES"

        binding.deleteSubjectButton.visibility = View.VISIBLE
        binding.deleteSubjectButton.setOnClickListener {
            deleteSubject()
        }
    }

    private fun deleteSubject() {
        val prompt = AlertDialog.Builder(requireContext()).apply {
            setMessage("Are you sure you want to delete $code?")
                .setPositiveButton("Yes") { _, _ ->
                    subjectStorage.deleteSubject(code!!)
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("No") { _, _ ->
                }
            create()
        }
        prompt.show()
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(message)
                .setPositiveButton("Ok") { _, _ ->
                }
            create()
        }.show()
    }

}