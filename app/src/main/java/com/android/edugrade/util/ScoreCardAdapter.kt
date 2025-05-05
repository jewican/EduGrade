package com.android.edugrade.util

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.edugrade.R
import com.android.edugrade.databinding.ScoresScoreCardBinding
import com.android.edugrade.models.Score

class ScoreCardAdapter(
    private val scoresList: List<Score>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ScoreCardAdapter.ScoreCardHolder>() {
    private val df = DecimalFormat("#.##")

        class ScoreCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding = ScoresScoreCardBinding.bind(itemView)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreCardHolder {
        val scoreView = LayoutInflater.from(parent.context)
            .inflate(R.layout.scores_score_card, parent, false)

        return ScoreCardHolder(scoreView)
    }

    override fun getItemCount() = scoresList.size

    override fun onBindViewHolder(holder: ScoreCardHolder, position: Int) {
        holder.binding.scoreName.text = scoresList[position].name
        holder.binding.scoreSubjectCode.text = scoresList[position].code
        holder.binding.userScore.text = df.format(scoresList[position].userScore)
        holder.binding.totalScore.text = df.format(scoresList[position].totalScore)
        holder.binding.root.setOnClickListener { onClick(position) }
    }
}