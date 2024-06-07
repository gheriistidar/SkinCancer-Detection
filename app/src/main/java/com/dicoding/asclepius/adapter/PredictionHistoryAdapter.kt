package com.dicoding.asclepius.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.ItemPredictionHistoryBinding

class PredictionHistoryAdapter(private val predictionHistoryList: List<PredictionHistory>) :
    RecyclerView.Adapter<PredictionHistoryAdapter.PredictionHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionHistoryViewHolder {
        val binding =
            ItemPredictionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredictionHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionHistoryViewHolder, position: Int) {
        val predictionHistory = predictionHistoryList[position]
        holder.bind(predictionHistory)
    }

    override fun getItemCount() = predictionHistoryList.size

    inner class PredictionHistoryViewHolder(private val binding: ItemPredictionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(predictionHistory: PredictionHistory) {
            binding.imageView.setImageURI(Uri.parse(predictionHistory.imageUri))
            binding.predictionTextView.text = predictionHistory.prediction
            binding.confidenceScoreTextView.text = "${predictionHistory.confidenceScore}%"
        }
    }
}