package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.database.PredictionHistoryDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var resultImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultImage = findViewById(R.id.result_image)
        resultText = findViewById(R.id.result_text)
        saveButton = findViewById(R.id.saveButton)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getStringExtra("PREDICTION")
        val confidenceScore = intent.getIntExtra("CONFIDENCE_SCORE", 0)

        resultImage.setImageURI(Uri.parse(imageUri))
        resultText.text = "$prediction ($confidenceScore%)"

        saveButton.setOnClickListener {
            savePredictionHistory(imageUri, prediction, confidenceScore)
        }
    }

    private fun savePredictionHistory(
        imageUri: String?,
        prediction: String?,
        confidenceScore: Int
    ) {
        val predictionHistory = PredictionHistory(
            imageUri = imageUri ?: "",
            prediction = prediction ?: "",
            confidenceScore = confidenceScore
        )

        val db = PredictionHistoryDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.predictionHistoryDao().insert(predictionHistory)

                val allPredictionHistories = db.predictionHistoryDao().getAll()

                if (allPredictionHistories.size > 6) {
                    val oldestPredictionHistory = allPredictionHistories.last()
                    db.predictionHistoryDao().delete(oldestPredictionHistory)
                }

                runOnUiThread {
                    showToast("Telah Tersimpan")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Terjadi kesalahan: ${e.message}")
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
