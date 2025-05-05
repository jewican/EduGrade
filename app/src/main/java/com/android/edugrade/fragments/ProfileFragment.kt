package com.android.edugrade.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.edugrade.R
import com.android.edugrade.activities.DevPageActivity
import com.android.edugrade.activities.LoginActivity
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.databinding.FragmentProfileBinding
import com.android.edugrade.util.saveToFile
import com.android.edugrade.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    @Inject
    lateinit var subjectStorage: SubjectStorage
    @Inject
    lateinit var scoreStorage: ScoreStorage
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etEmail.focusable = View.NOT_FOCUSABLE

        lifecycleScope.launch {
            binding.etEmail.setText(userRepository.getEmail())
            binding.etUsername.setText(userRepository.getUsername())
            applyTargetGpa(userRepository.getTargetGpa())
        }

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val selectedButton = binding.toggleButton.findViewById<Button>(checkedId)
            when (checkedId) {
                R.id.custom -> {
                    binding.customInputLayout.visibility = View.VISIBLE
                }
                else -> {
                    binding.customInputLayout.visibility = View.GONE
                    val selectedGpa = selectedButton.tag.toString().toDouble()
                    userRepository.setTargetGpa(selectedGpa)
                }
            }
        }

        binding.btnSaveCustomGpa.setOnClickListener {
            val customGpa = binding.customTargetGpaInput.text.toString().toDouble()
            userRepository.setTargetGpa(customGpa)
        }

        binding.exportSubjectsButton.setOnClickListener {
            subjectStorage.exportSubjectsToJson(
                onSuccess = { jsonString ->
                    val uri = jsonString.saveToFile(requireContext(), "subjects_export_${LocalDateTime.now()}.json")
                    if (uri != null) {
                        showDialog("Subjects exported to Documents successfully!")
                    }
                },
                onFailure = { exception ->
                    showDialog("Export failed: ${exception.message}")
                }
            )
        }

        binding.exportScoresButton.setOnClickListener {
            scoreStorage.exportScoresToJson(
                onSuccess = { jsonString ->
                    val uri = jsonString.saveToFile(requireContext(), "scores_export_${LocalDateTime.now()}.json")
                    if (uri != null) {
                        showDialog("Scores exported to Documents successfully!")
                    }
                },
                onFailure = { exception ->
                    showDialog("Export failed: ${exception.message}")
                }
            )
        }

        binding.btnAboutUs.setOnClickListener {
            startActivity(Intent(requireContext(), DevPageActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        val message = "Are you sure you want to log out?"

        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                userRepository.signOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No") { _, _ -> }
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_surface)
        dialog.show()
    }

    fun applyTargetGpa(targetGpa: Double) {
        val toggleGroup = binding.toggleButton
        var matched = false

        for (i in 0 until toggleGroup.childCount) {
            val button = toggleGroup.getChildAt(i) as? Button
            val tagValue = button?.tag?.toString()?.toDoubleOrNull()

            if (tagValue != null && tagValue == targetGpa) {
                toggleGroup.check(button.id)
                matched = true
                break
            }
        }

        if (!matched) {
            toggleGroup.check(R.id.custom)
            binding.customInputLayout.visibility = View.VISIBLE
            binding.customTargetGpaInput.setText(targetGpa.toString())
        }
    }
}