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
import com.android.edugrade.databinding.AddSubjectActivityTypesItemBinding
import com.android.edugrade.databinding.AddSubjectTimeslotItemBinding
import com.android.edugrade.databinding.FragmentAddSubjectBinding
import com.android.edugrade.models.AssessmentType
import com.android.edugrade.models.Subject
import com.android.edugrade.models.Timeslot
import com.android.edugrade.util.SubjectStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddSubjectFragment(
    val code: String? = null
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

        binding.timeslotsCard.addTimeslotButton.setOnClickListener {
            addTimeslot()
        }

        binding.activityTypesCard.addTypeButton.setOnClickListener {
            addActivityType()
        }

        binding.addSubjectButton.setOnClickListener {
            if (saveSubject()) parentFragmentManager.popBackStack()
        }

        if (code != null) {
            editSubjectMode()
            return
        }

        addTimeslot()
        addActivityType()
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

                timeslots.add(
                    Timeslot(
                        type = child.timeslotType.text.toString(),
                        dayOfWeek = DayOfWeek.MONDAY, // TODO: add day of week selector in layout
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
        val linearLayout = binding.activityTypesCard.subjectActivityTypes
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

            timeslotBinding.timeslotType.setText(subject.timeslots[i].type)
            timeslotBinding.timeslotTimeIn.setText(subject.timeslots[i].startTime.toString())
            timeslotBinding.timeslotTimeOut.setText(subject.timeslots[i].endTime.toString())
        }

        var activityTypeView: View
        var activityTypeBinding: AddSubjectActivityTypesItemBinding
        for (i in 0 until subject.assessmentTypes.size) {
            addActivityType()

            activityTypeView = binding.activityTypesCard.subjectActivityTypes.getChildAt(i)
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