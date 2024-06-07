package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val timestamp = System.currentTimeMillis()
                val uCrop = UCrop.of(it, Uri.fromFile(File(cacheDir, "temp_$timestamp.jpg")))

                uCrop.start(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            if (currentImageUri != null) {
                analyzeImage()
            } else {
                showToast("Tidak Ada Gambar Terpilih")
            }
        }

        binding.historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.newsButton.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGallery() {
        getContent.launch("image/*")
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(null)
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        binding.progressIndicator.visibility = View.VISIBLE // Show the progress bar
        currentImageUri?.let {
            try {
                ImageClassifierHelper(
                    context = this,
                    classifierListener = this
                ).classifyStaticImage(it)
            } catch (e: Exception) {
                showToast("Terjadi kesalahan saat inferensi: ${e.message}")
                binding.progressIndicator.visibility = View.GONE // Hide the progress bar
            }
        }
    }


    private fun moveToResult(prediction: String, confidenceScore: Int) {
        currentImageUri?.let {
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("IMAGE_URI", it.toString())
                putExtra("PREDICTION", prediction)
                putExtra("CONFIDENCE_SCORE", confidenceScore)
            }
            startActivity(intent)
        }
    }

    override fun onError(error: String) {
        showToast(error)
        binding.progressIndicator.visibility = View.GONE
    }

    override fun onResults(results: List<Classifications>?) {
        results?.get(0)?.categories?.maxByOrNull { it.score }?.let { category ->
            val prediction = category.label
            val confidenceScore = (category.score * 100).toInt()
            moveToResult(prediction, confidenceScore)
        }
        binding.progressIndicator.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            val resultUri = UCrop.getOutput(data)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            }
        }
    }
}
