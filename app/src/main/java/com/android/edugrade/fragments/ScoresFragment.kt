package com.android.edugrade.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.edugrade.R
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.databinding.FragmentScoresBinding
import com.android.edugrade.databinding.ScoreDetailsDialogBinding
import com.android.edugrade.models.Score
import com.android.edugrade.util.ScoreCardAdapter
import com.android.edugrade.util.format
import com.android.edugrade.util.setCurrentFragment
import com.android.edugrade.util.showDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScoresFragment : Fragment(R.layout.fragment_scores) {
    private lateinit var binding: FragmentScoresBinding
    @Inject
    lateinit var scoreStorage: ScoreStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoresBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val scores = scoreStorage.getAllScores()

        binding.addScoreButton.setOnClickListener {
            setCurrentFragment(AddScoreFragment())
        }

        binding.scoresList.apply { 
            layoutManager = LinearLayoutManager(context)
            adapter = ScoreCardAdapter(
                scoresList = scores,
                onClick = {
                    showScoreDialog(scores[it])
                }
            )
        }
    }

    private fun showScoreDialog(score: Score) {
        val dialogBinding = ScoreDetailsDialogBinding.inflate(layoutInflater).apply {
            tvName.text = score.name
            tvCode.text = score.code
            tvUserScore.text = score.userScore.toString()
            tvTotalScore.text = score.totalScore.toString()
            tvDateAdded.text = score.dateAdded.format()

            btnDelete.setOnClickListener {
                btnDelete.visibility = View.GONE
                deletePromptView.visibility = View.VISIBLE
            }

            cancelDelete.setOnClickListener {
                btnDelete.visibility = View.VISIBLE
                deletePromptView.visibility = View.GONE
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_surface)

        dialogBinding.confirmDelete.setOnClickListener {
            scoreStorage.deleteScore(
                scoreId = score.id,
                onSuccess = {
                    showDialog("Score deleted successfully.")
                    binding.scoresList.adapter?.notifyDataSetChanged()
                    dialog.dismiss()
                },
                onFailure = { showDialog("Error deleting score: ${it.message}") })
        }

        dialog.show()
    }
}