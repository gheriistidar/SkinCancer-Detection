package com.dicoding.asclepius.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.PredictionHistoryAdapter
import com.dicoding.asclepius.database.PredictionHistoryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val db = PredictionHistoryDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val predictionHistoryList = db.predictionHistoryDao().getAll()
            runOnUiThread {
                val adapter = PredictionHistoryAdapter(predictionHistoryList)
                recyclerView.adapter = adapter
            }
        }
    }
}
