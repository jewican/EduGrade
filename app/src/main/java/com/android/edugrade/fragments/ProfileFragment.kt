package com.android.edugrade.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.custom && isChecked) {
                binding.customInputLayout.visibility = View.VISIBLE
            } else {
                binding.customInputLayout.visibility = View.GONE
            }
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
}